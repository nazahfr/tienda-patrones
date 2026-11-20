package pe.utp.tienda.behavioral.observer;

import java.util.ArrayList;
import java.util.List;

/** Subject (GoF): mantiene la lista de suscriptores y los notifica (relacion uno-a-muchos). */
public abstract class Sujeto<E> {
    private List<Observador<E>> observadores = new ArrayList<>();

    public void suscribir(Observador<E> o) {
        if (o != null && !observadores.contains(o)) observadores.add(o);
    }

    public void desuscribir(Observador<E> o) { observadores.remove(o); }

    public int cantidadObservadores() { return observadores.size(); }

    protected void notificar(E evento) {
        for (Observador<E> o : new ArrayList<>(observadores)) { // copia: un observador puede desuscribirse al ser notificado
            o.actualizar(evento);
        }
    }

    /** Usado al clonar el sujeto: la copia no hereda los suscriptores del original. */
    protected void reiniciarObservadores() { observadores = new ArrayList<>(); }
}
