package pe.utp.tienda.behavioral.memento;

import java.util.ArrayDeque;
import java.util.Deque;

/** Caretaker (GoF): guarda los mementos del borrador y los devuelve en orden para deshacer/rehacer. Nunca los abre. */
public class HistorialBorrador {
    private final Deque<BorradorPedido.Memento> paraDeshacer = new ArrayDeque<>();
    private final Deque<BorradorPedido.Memento> paraRehacer = new ArrayDeque<>();

    /** Se llama ANTES de cada edicion: congela el estado actual. */
    public void respaldar(BorradorPedido borrador) {
        paraDeshacer.push(borrador.guardar());
        paraRehacer.clear();
    }

    public boolean deshacer(BorradorPedido borrador) {
        if (paraDeshacer.isEmpty()) return false;
        paraRehacer.push(borrador.guardar());
        borrador.restaurar(paraDeshacer.pop());
        return true;
    }

    public boolean rehacer(BorradorPedido borrador) {
        if (paraRehacer.isEmpty()) return false;
        paraDeshacer.push(borrador.guardar());
        borrador.restaurar(paraRehacer.pop());
        return true;
    }

    public int versionesGuardadas() { return paraDeshacer.size(); }
}
