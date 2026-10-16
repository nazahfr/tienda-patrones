package pe.utp.tienda.structural.adapter;

/** Adapter: traduce nuestra interfaz (validarDni) a la del servicio externo (consultarPersona). */
public class ReniecAdapter implements ValidadorDocumento {
    private final ReniecApiExterna api;

    public ReniecAdapter(ReniecApiExterna api) { this.api = api; }

    @Override
    public boolean validarDni(String dni) {
        return api.consultarPersona(dni, "DNI").startsWith("OK");
    }
}
