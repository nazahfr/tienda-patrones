package pe.utp.tienda.creational.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Singleton: una unica puerta de acceso a MySQL (bd_tienda_ropa) para toda la aplicacion. */
public final class ConexionBD {
    private static volatile ConexionBD instancia;
    private final String url = "jdbc:mysql://localhost:3306/bd_tienda_ropa";
    private final String usuario = "root";
    private final String clave = System.getenv().getOrDefault("DB_PASSWORD", "");
    private Connection conexion;

    private ConexionBD() { }

    public static ConexionBD getInstancia() {
        if (instancia == null) {                       // 1.a comprobacion (sin bloqueo)
            synchronized (ConexionBD.class) {
                if (instancia == null) {               // 2.a comprobacion (con bloqueo)
                    instancia = new ConexionBD();
                }
            }
        }
        return instancia;
    }

    public synchronized Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(url, usuario, clave);
        }
        return conexion;
    }

    public String getUrl() { return url; }
}
