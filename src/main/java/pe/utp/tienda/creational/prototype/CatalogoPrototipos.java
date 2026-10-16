package pe.utp.tienda.creational.prototype;

import java.util.HashMap;
import java.util.Map;
import pe.utp.tienda.model.Producto;

/** Registro de prototipos: guarda un producto "plantilla" por linea y entrega variantes clonadas. */
public class CatalogoPrototipos {
    private final Map<String, Producto> prototipos = new HashMap<>();

    public void registrar(String clave, Producto prototipo) {
        prototipos.put(clave, prototipo);
    }

    public Producto crearVariante(String clave, String nuevoSku, String talla, int stock) {
        Producto base = prototipos.get(clave);
        if (base == null) throw new IllegalArgumentException("No existe prototipo: " + clave);
        return base.clonarVariante(nuevoSku, talla, stock);
    }
}
