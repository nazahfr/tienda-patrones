package pe.utp.tienda.structural.proxy;

import java.util.Optional;
import pe.utp.tienda.model.Producto;
import pe.utp.tienda.persistence.ProductoDAO;

/** RealSubject (GoF): cada consulta va a la capa de datos (en produccion, un SELECT a MySQL). Cuenta sus accesos. */
public class CatalogoRepositorio implements ConsultaCatalogo {
    private final ProductoDAO dao;
    private int accesos = 0;

    public CatalogoRepositorio(ProductoDAO dao) { this.dao = dao; }

    @Override
    public Optional<Producto> buscarPorSku(String sku) {
        accesos++;
        return dao.buscarPorSku(sku);
    }

    public int getAccesos() { return accesos; }
}
