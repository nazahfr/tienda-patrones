package pe.utp.tienda.persistence;

import java.util.List;
import pe.utp.tienda.model.Pedido;

/** Capa de acceso a datos (DAO) para los pedidos. Asigna el id al guardar. */
public interface PedidoDAO {
    void guardar(Pedido pedido);
    List<Pedido> listar();
}
