package pe.utp.tienda.structural.adapter;

/** Interfaz Target: la que el sistema espera (independiente de cualquier proveedor externo). */
public interface ValidadorDocumento {
    boolean validarDni(String dni);
}
