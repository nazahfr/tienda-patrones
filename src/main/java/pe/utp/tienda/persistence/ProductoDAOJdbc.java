package pe.utp.tienda.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import pe.utp.tienda.creational.singleton.ConexionBD;
import pe.utp.tienda.model.*;

/** DAO sobre MySQL (tabla PRODUCTO de bd_tienda_ropa). Usa la conexion unica del Singleton ConexionBD. */
public class ProductoDAOJdbc implements ProductoDAO {

    @Override
    public void guardar(Producto p) {
        String sql = "INSERT INTO PRODUCTO (sku, nombre, tipo, talla, precio, stock, stock_minimo) VALUES (?,?,?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), talla=VALUES(talla), precio=VALUES(precio), "
                + "stock=VALUES(stock), stock_minimo=VALUES(stock_minimo)";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, p.getSku());
            ps.setString(2, nombreBase(p));
            ps.setString(3, p.tipo());
            ps.setString(4, p.getTalla());
            ps.setDouble(5, p.getPrecio());
            ps.setInt(6, p.getStock());
            ps.setInt(7, p.getStockMinimo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar el producto " + p.getSku() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Producto> buscarPorSku(String sku) {
        try (PreparedStatement ps = conexion().prepareStatement("SELECT * FROM PRODUCTO WHERE sku = ?")) {
            ps.setString(1, sku);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar el SKU " + sku + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement("SELECT * FROM PRODUCTO ORDER BY sku");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo listar el catalogo: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public void actualizarStock(String sku, int nuevoStock) {
        try (PreparedStatement ps = conexion().prepareStatement("UPDATE PRODUCTO SET stock = ? WHERE sku = ?")) {
            ps.setInt(1, nuevoStock);
            ps.setString(2, sku);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo actualizar el stock de " + sku + ": " + e.getMessage(), e);
        }
    }

    private Connection conexion() throws SQLException { return ConexionBD.getInstancia().getConexion(); }

    /** getNombre() ya trae la talla entre parentesis (por el Composite); en la BD se guarda solo el nombre base. */
    private String nombreBase(Producto p) {
        String n = p.getNombre();
        String sufijo = " (" + p.getTalla() + ")";
        return n.endsWith(sufijo) ? n.substring(0, n.length() - sufijo.length()) : n;
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        String sku = rs.getString("sku"), nombre = rs.getString("nombre"), talla = rs.getString("talla");
        double precio = rs.getDouble("precio");
        int stock = rs.getInt("stock");
        Producto p = switch (rs.getString("tipo")) {
            case "Polera" -> new Polera(sku, nombre, talla, precio, stock);
            case "Pantalon" -> new Pantalon(sku, nombre, talla, precio, stock);
            case "Zapatilla" -> new Zapatilla(sku, nombre, talla, precio, stock);
            default -> throw new IllegalStateException("Tipo de producto desconocido: " + rs.getString("tipo"));
        };
        p.setStockMinimo(rs.getInt("stock_minimo"));
        return p;
    }
}
