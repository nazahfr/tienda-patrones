package pe.utp.tienda.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import pe.utp.tienda.behavioral.state.EstadoPedido;
import pe.utp.tienda.creational.singleton.ConexionBD;
import pe.utp.tienda.model.*;

/** DAO sobre MySQL (tablas CLIENTE, PEDIDO y DETALLE_PEDIDO). Guarda y lee el estado como texto y lo reconstruye con State. */
public class PedidoDAOJdbc implements PedidoDAO {
    private final ProductoDAO productos;

    public PedidoDAOJdbc(ProductoDAO productos) { this.productos = productos; }

    @Override
    public void guardar(Pedido pedido) {
        try {
            Connection cn = ConexionBD.getInstancia().getConexion();
            boolean autoCommit = cn.getAutoCommit();
            cn.setAutoCommit(false);
            try {
                guardarCliente(cn, pedido.getCliente());
                if (pedido.getId() == 0) insertar(cn, pedido); else actualizarEstado(cn, pedido);
                cn.commit();
            } catch (SQLException | RuntimeException e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(autoCommit);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar el pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pedido> listar() {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.estado, p.descuento_pct, p.costo_envio, p.notas, c.dni, c.nombres, c.apellidos "
                + "FROM PEDIDO p JOIN CLIENTE c ON c.dni = p.dni_cliente ORDER BY p.id";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Pedido.Builder b = new Pedido.Builder(new Cliente(rs.getString("dni"), rs.getString("nombres"), rs.getString("apellidos")))
                        .descuento(rs.getDouble("descuento_pct"))
                        .envio(rs.getDouble("costo_envio"))
                        .notas(rs.getString("notas") == null ? "" : rs.getString("notas"));
                int id = rs.getInt("id");
                cargarItems(id, b);
                Pedido pedido = b.build();
                pedido.setId(id);
                pedido.restaurarEstado(EstadoPedido.de(rs.getString("estado")));
                lista.add(pedido);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo listar los pedidos: " + e.getMessage(), e);
        }
        return lista;
    }

    private void guardarCliente(Connection cn, Cliente c) throws SQLException {
        String[] partes = c.getNombreCompleto().split(" ", 2);
        try (PreparedStatement ps = cn.prepareStatement(
                "INSERT INTO CLIENTE (dni, nombres, apellidos) VALUES (?,?,?) ON DUPLICATE KEY UPDATE dni = dni")) {
            ps.setString(1, c.getDni());
            ps.setString(2, partes[0]);
            ps.setString(3, partes.length > 1 ? partes[1] : "");
            ps.executeUpdate();
        }
    }

    private void insertar(Connection cn, Pedido p) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "INSERT INTO PEDIDO (dni_cliente, estado, descuento_pct, costo_envio, notas) VALUES (?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getCliente().getDni());
            ps.setString(2, p.getEstado());
            ps.setDouble(3, p.getDescuentoPct());
            ps.setDouble(4, p.getCostoEnvio());
            ps.setString(5, p.getNotas());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
        }
        try (PreparedStatement ps = cn.prepareStatement(
                "INSERT INTO DETALLE_PEDIDO (id_pedido, sku, cantidad) VALUES (?,?,?)")) {
            for (ItemPedido i : p.getItems()) {
                ps.setInt(1, p.getId());
                ps.setString(2, i.getProducto().getSku());
                ps.setInt(3, i.getCantidad());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void actualizarEstado(Connection cn, Pedido p) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("UPDATE PEDIDO SET estado = ? WHERE id = ?")) {
            ps.setString(1, p.getEstado());
            ps.setInt(2, p.getId());
            ps.executeUpdate();
        }
    }

    private void cargarItems(int idPedido, Pedido.Builder b) throws SQLException {
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion()
                .prepareStatement("SELECT sku, cantidad FROM DETALLE_PEDIDO WHERE id_pedido = ?")) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto prod = productos.buscarPorSku(rs.getString("sku"))
                            .orElseThrow(() -> new IllegalStateException("Producto inexistente en el pedido " + idPedido));
                    b.agregarItem(prod, rs.getInt("cantidad"));
                }
            }
        }
    }
}
