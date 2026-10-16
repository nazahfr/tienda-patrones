package pe.utp.tienda.model;

public class Cliente {
    private final String dni;
    private final String nombres;
    private final String apellidos;

    public Cliente(String dni, String nombres, String apellidos) {
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
    }

    public String getDni() { return dni; }
    public String getNombreCompleto() { return nombres + " " + apellidos; }
}
