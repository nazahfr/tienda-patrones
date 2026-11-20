package pe.utp.tienda.structural.proxy;

import java.util.Optional;
import pe.utp.tienda.model.Producto;

/** Subject (GoF) de la consulta de catalogo: la usan por igual el repositorio real y el proxy con cache. */
public interface ConsultaCatalogo {
    Optional<Producto> buscarPorSku(String sku);
}
