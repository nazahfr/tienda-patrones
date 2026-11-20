package pe.utp.tienda.structural.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import pe.utp.tienda.model.Producto;

/** Proxy de cache (GoF): responde desde memoria las consultas repetidas y solo llama al real cuando no tiene el SKU. */
public class CatalogoCacheProxy implements ConsultaCatalogo {
    private final ConsultaCatalogo real;
    private final Map<String, Producto> cache = new HashMap<>();
    private int aciertos = 0, fallos = 0;

    public CatalogoCacheProxy(ConsultaCatalogo real) { this.real = real; }

    @Override
    public Optional<Producto> buscarPorSku(String sku) {
        Producto enCache = cache.get(sku);
        if (enCache != null) {
            aciertos++;
            return Optional.of(enCache);
        }
        fallos++;
        Optional<Producto> p = real.buscarPorSku(sku);
        p.ifPresent(prod -> cache.put(sku, prod));
        return p;
    }

    public void invalidar(String sku) { cache.remove(sku); }
    public void limpiar() { cache.clear(); }
    public int getAciertos() { return aciertos; }
    public int getFallos() { return fallos; }
}
