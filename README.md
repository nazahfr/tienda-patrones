# Sistema de Gestión de Ventas e Inventario – Tienda de Ropa (UTP · Diseño de Patrones · PC3)

Aplicación en **Java 17** con arquitectura en capas y **15 patrones de diseño** (Semanas 6 a 15), interfaz gráfica **Swing**, consola de demostración y pruebas automatizadas.

| Entrega | Patrones |
| :--- | :--- |
| PC2 (Semanas 6–9) | Singleton, Prototype, Builder, Factory Method, Abstract Factory, Adapter, Facade, Decorator, Composite |
| **PC3 (Semanas 11–15)** | **Proxy, Bridge** (estructurales) · **State, Observer, Command, Memento** (de comportamiento) · Template Method (usado en la refactorización de antipatrones) |

---

## 1. Repositorio y requisitos
* **Repositorio:** https://github.com/nazahfr/tienda-patrones · **Rama principal:** `main`
* **JDK 17+** (`java -version`), **Maven 3.8+** (`mvn -version`), **Git**. MySQL 8 es opcional (ver sección 4).

```bash
git clone https://github.com/nazahfr/tienda-patrones.git
cd tienda-patrones
mvn clean compile          # con Maven
# o sin Maven:
mkdir -p out && javac --release 17 -encoding UTF-8 -d out $(find src/main -name "*.java")
```

## 2. Cómo ejecutarlo
| Qué | Comando |
| :--- | :--- |
| **Interfaz gráfica** (15 patrones, un panel por patrón) | `iniciar-gui.bat` (Windows) · `java -cp target/classes pe.utp.tienda.gui.DemoGUI` |
| Demo en consola PC3 (State, Observer, Command, Memento, Proxy, Bridge) | `java -cp target/classes pe.utp.tienda.DemoPC3` |
| Demo en consola PC2 | `java -cp target/classes pe.utp.tienda.Demo` |
| Verificaciones sin Maven (14 comprobaciones PC2) | `java -cp target/classes pe.utp.tienda.Pruebas` |
| **Pruebas JUnit 5** (9 de la PC2 + 28 de la PC3) | `mvn test` |

Si usas `javac` en lugar de Maven, reemplaza `target/classes` por `out`.

## 3. Arquitectura
Presentación (Swing) → **Controladores** (`VentaController`) → Lógica de negocio (`VentaFacade`, servicios) → **DAO** (`ProductoDAO`, `PedidoDAO`: memoria o JDBC/MySQL).
`ContextoTienda` es la **raíz de composición**: el único lugar que hace `new` de servicios e inyecta dependencias por constructor. Diagrama: `docs/uml_arquitectura.png`.

## 4. Persistencia
Por defecto la aplicación usa DAO **en memoria** (no necesita base de datos, con tres prendas de ejemplo). Para usar MySQL:
1. Ejecuta `sql/schema.sql` (crea `bd_tienda_ropa` con las tablas que usa el código).
2. Define la contraseña: `DB_PASSWORD=...` (usuario `root`, URL `jdbc:mysql://localhost:3306/bd_tienda_ropa`, en `ConexionBD`).
3. Ejecuta con `TIENDA_PERSISTENCIA=mysql`.

> Los DAO JDBC compilan, pero **no fueron ejecutados contra un servidor MySQL real** al prepararse esta entrega. Pruébalos antes de la sustentación.

## 5. Matriz de patrones
### PC3
| Patrón | Clases | Aplicación en la tienda |
| :--- | :--- | :--- |
| **State** | `EstadoPedido`, `EstadoBase`, `EstadoPendiente/Pagado/EnPreparacion/Enviado/Entregado/Cancelado`, `Pedido` | Ciclo de vida del pedido; cada estado decide qué transiciones permite y rechaza el resto con `TransicionInvalidaException`. |
| **Observer** | `Sujeto<E>`, `Observador<E>`, `NotificadorClienteObserver`, `AuditoriaObserver`, `DashboardObserver`, `ComprasStockObserver` | `Pedido` publica cada cambio de estado; `InventarioService` publica la alerta de stock mínimo. |
| **Command** | `Comando`, `DescontarStockComando`, `ReponerStockComando`, `ComandoCompuesto`, `InvocadorComandos` | Operaciones de inventario con deshacer/rehacer, cola de tareas y bitácora de auditoría. |
| **Memento** | `BorradorPedido` (+ `Memento`), `HistorialBorrador` | Deshacer/rehacer en la edición del pedido (carrito); el memento no expone métodos públicos. |
| **Proxy** | `ServicioReportesProxy` (protección y auditoría), `CatalogoCacheProxy` (caché) | El ADMIN ve ventas, el VENDEDOR solo inventario; las consultas repetidas del catálogo no vuelven a la BD. |
| **Bridge** | `Reporte` → `ReporteVentas`/`ReporteInventario`; `ExportadorReporte` → `ExportadorHtml`/`Csv`/`Json` | 2 reportes × 3 formatos con 5 clases en lugar de 6. |

### PC2 (sin cambios de comportamiento)
Singleton (`ConfiguracionApp`, `ConexionBD`), Prototype (`Producto`, `Pedido`), Builder (`Pedido.Builder`), Factory Method (`ComprobanteFactory`), Abstract Factory (`PasarelaPagoFactory`: Yape, Plin, Tarjeta), Adapter (`ReniecAdapter`), Facade (`VentaFacade`), Decorator (`ConDescuento`, `ConIgv`, `ConEnvio`), Composite (`Categoria`, `Producto`).

## 6. Antipatrones detectados y refactorizados
| Antipatrón | Evidencia en la PC2 | Refactorización |
| :--- | :--- | :--- |
| **Copy-Paste** | `cobrar()` idéntico en `YapePago`/`PlinPago`/`TarjetaPago`; mismo `String.format` en los 3 recibos; mismo correlativo en `Boleta`/`FacturaElectronica` | Template Method: `PagoConLimite`, `ReciboBase`, `ComprobanteBase` (`docs/uml_refactor_templatemethod.png`). Salida idéntica. |
| **God Object** | `DemoGUI` (1137 líneas) instanciaba servicios, armaba `Cliente`/`Pedido` y llamaba a la Facade desde los botones | Servicios inyectados desde `ContextoTienda`; las ventas pasan por `VentaController`; los paneles nuevos están en clases separadas. **Mitigado, no eliminado**: `DemoGUI` sigue siendo grande. |
| **Lava Flow** | README describía clases inexistentes (`CulqiFactory`, `MercadoPagoFactory`, `PrecioDecorator`…) y `ConexionBD` no la usaba ningún DAO | README reescrito según el código real; `ConexionBD` ahora la usan los DAO JDBC. |

## 7. Estructura
```text
src/main/java/pe/utp/tienda/
├── app/          ContextoTienda (raíz de composición)
├── behavioral/   state · observer · command · memento          (PC3)
├── controller/   VentaController, SolicitudVenta, ResultadoVenta
├── creational/   singleton · prototype · factory · abstractfactory
├── structural/   adapter · facade · decorator · composite · proxy · bridge
├── persistence/  ProductoDAO, PedidoDAO (memoria y JDBC)
├── model/  service/  gui/
├── Demo.java  DemoPC3.java  Pruebas.java
src/test/java/pe/utp/tienda/   PatronesTest (PC2) · PatronesPC3Test (PC3)
docs/   diagramas UML (.png/.dot) y generadores (gen_uml.py, gen_uml_pc3.py)
sql/    schema.sql
```
