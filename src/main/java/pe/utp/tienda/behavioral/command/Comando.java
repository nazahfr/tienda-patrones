package pe.utp.tienda.behavioral.command;

/** Command (GoF): una operacion de negocio encapsulada como objeto, reversible y auditable. */
public interface Comando {
    void ejecutar();
    void deshacer();
    String descripcion();
}
