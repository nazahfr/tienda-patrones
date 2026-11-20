package pe.utp.tienda.persistence;

import java.util.List;
import java.util.Optional;
import pe.utp.tienda.model.Producto;

/** Capa de acceso a datos (DAO) para el catalogo. Hay una version en memoria y otra JDBC/MySQL. */
public interface ProductoDAO {
    void guardar(Producto producto);
    Optional<Producto> buscarPorSku(String sku);
    List<Producto> listar();
    void actualizarStock(String sku, int nuevoStock);
}
