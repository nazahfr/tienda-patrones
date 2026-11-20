package pe.utp.tienda.behavioral.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Observador concreto: deja un rastro de auditoria de cada transicion de estado. */
public class AuditoriaObserver implements Observador<CambioEstadoPedido> {
    private final List<String> registros = new ArrayList<>();

    @Override
    public void actualizar(CambioEstadoPedido e) {
        registros.add(String.format("AUDITORIA | pedido de %s: %s -> %s",
                e.pedido().getCliente().getNombreCompleto(), e.estadoAnterior(), e.estadoNuevo()));
    }

    public List<String> getRegistros() { return Collections.unmodifiableList(registros); }
}
