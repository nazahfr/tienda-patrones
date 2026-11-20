package pe.utp.tienda.model;

/** Personal que opera el sistema. Lo consulta el Proxy de seguridad de reportes. */
public record Usuario(String nombre, Rol rol) {
    @Override public String toString() { return nombre + " (" + rol + ")"; }
}
