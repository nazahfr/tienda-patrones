package pe.utp.tienda.structural.adapter;

/**
 * Adaptee: simula un servicio de terceros con una interfaz incompatible con la nuestra.
 * En produccion esta clase seria el cliente HTTP real de la API de consulta de DNI.
 */
public class ReniecApiExterna {
    /** Devuelve "OK|NOMBRE" si existe, o "ERROR|NO_ENCONTRADO". */
    public String consultarPersona(String numeroDocumento, String tipoDocumento) {
        if ("DNI".equals(tipoDocumento) && numeroDocumento != null && numeroDocumento.matches("\\d{8}")) {
            return "OK|PERSONA REGISTRADA";
        }
        return "ERROR|NO_ENCONTRADO";
    }
}
