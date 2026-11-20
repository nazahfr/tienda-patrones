package pe.utp.tienda.behavioral.observer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Observador concreto: mantiene los contadores que muestra el panel de control (cuantos pedidos llegaron a cada estado). */
public class DashboardObserver implements Observador<CambioEstadoPedido> {
    private final Map<String, Integer> porEstado = new LinkedHashMap<>();

    @Override
    public void actualizar(CambioEstadoPedido e) {
        porEstado.merge(e.estadoNuevo(), 1, Integer::sum);
    }

    public Map<String, Integer> getPorEstado() { return Collections.unmodifiableMap(porEstado); }

    public String resumen() { return porEstado.isEmpty() ? "(sin movimientos)" : porEstado.toString(); }
}
