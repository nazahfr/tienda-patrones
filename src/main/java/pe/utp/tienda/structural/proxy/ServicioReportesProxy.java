package pe.utp.tienda.structural.proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pe.utp.tienda.model.Rol;
import pe.utp.tienda.model.Usuario;

/**
 * Proxy de proteccion y auditoria (GoF): controla el acceso al servicio real segun el rol del usuario
 * y registra cada intento (permitido o denegado). Solo el ADMIN ve ventas; el VENDEDOR solo inventario.
 */
public class ServicioReportesProxy implements ServicioReportes {
    private final ServicioReportes real;
    private final Usuario usuario;
    private final List<String> auditoria = new ArrayList<>();

    public ServicioReportesProxy(ServicioReportes real, Usuario usuario) {
        this.real = real;
        this.usuario = usuario;
    }

    @Override
    public String generar(String tipo, String formato) {
        if (!tienePermiso(tipo)) {
            auditoria.add("DENEGADO  | " + usuario + " intento " + tipo + " en " + formato);
            throw new AccesoDenegadoException(usuario + " no tiene permiso para el reporte " + tipo);
        }
        String resultado = real.generar(tipo, formato);
        auditoria.add("PERMITIDO | " + usuario + " genero " + tipo + " en " + formato);
        return resultado;
    }

    private boolean tienePermiso(String tipo) {
        return usuario.rol() == Rol.ADMIN || "INVENTARIO".equalsIgnoreCase(tipo);
    }

    public List<String> getAuditoria() { return Collections.unmodifiableList(auditoria); }
}
