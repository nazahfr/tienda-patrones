package pe.utp.tienda.behavioral.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Observador concreto: el area de compras recibe una solicitud de reposicion cuando el stock llega al minimo. */
public class ComprasStockObserver implements Observador<AlertaStock> {
    private final List<String> solicitudes = new ArrayList<>();

    @Override
    public void actualizar(AlertaStock a) {
        solicitudes.add(String.format("COMPRAS | reponer %s - %s: stock %d, minimo %d",
                a.producto().getSku(), a.producto().getNombre(), a.stockActual(), a.stockMinimo()));
    }

    public List<String> getSolicitudes() { return Collections.unmodifiableList(solicitudes); }
}
