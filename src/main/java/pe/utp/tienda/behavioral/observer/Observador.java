package pe.utp.tienda.behavioral.observer;

/** Observer (GoF): recibe el evento cuando el sujeto cambia. E es el tipo de evento publicado. */
@FunctionalInterface
public interface Observador<E> {
    void actualizar(E evento);
}
