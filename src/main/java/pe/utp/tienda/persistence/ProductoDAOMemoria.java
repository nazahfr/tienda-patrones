package pe.utp.tienda.persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import pe.utp.tienda.model.Producto;

/** Implementacion en memoria: permite ejecutar la demo y las pruebas sin MySQL. */
public class ProductoDAOMemoria implements ProductoDAO {
    private final Map<String, Producto> datos = new LinkedHashMap<>();

    @Override public void guardar(Producto p) { datos.put(p.getSku(), p); }
    @Override public Optional<Producto> buscarPorSku(String sku) { return Optional.ofNullable(datos.get(sku)); }
    @Override public List<Producto> listar() { return new ArrayList<>(datos.values()); }

    @Override
    public void actualizarStock(String sku, int nuevoStock) {
        // Los objetos viven en memoria: el stock ya esta actualizado en el propio Producto.
        if (!datos.containsKey(sku)) throw new IllegalArgumentException("No existe el SKU " + sku);
    }
}
