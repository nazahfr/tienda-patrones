# Sistema de Gestión de Ventas e Inventario - Tienda de Ropa (UTP - PC2)

Implementación en **Java 17 Orientado a Objetos** con arquitectura basada en **Patrones de Diseño Creacionales y Estructurales** (Semanas 6 a 9). Incluye interfaz gráfica interactiva (**Java Swing**), consola de verificación y suite de pruebas unitarias automatizadas.

---

## 📌 SECCIÓN IV: REPOSITORIO Y MANUAL DE DESPLIEGUE

### 4.1. Enlace al Repositorio de Código Fuente
* **Repositorio GitHub:** [https://github.com/nazahfr/tienda-patrones](https://github.com/nazahfr/tienda-patrones)
* **Visibilidad:** Público / Con acceso para evaluación docente.
* **Rama principal:** `main`

---

### 4.2. Pasos para la Compilación y Ejecución en Entorno Local

#### A. Requisitos de Entorno de Desarrollo
Para compilar y ejecutar este proyecto se requiere contar con:
1. **Java Development Kit (JDK):** Versión 17 o superior.
   - Verificar en consola: `java -version` y `javac -version`
2. **Apache Maven:** Versión 3.8.0 o superior (recomendado).
   - Verificar en consola: `mvn -version`
3. **Git:** Para la clonación del repositorio.
4. **Sistema Operativo:** Windows 10/11, Linux o macOS.

---

#### B. Clonación del Repositorio
Abre una terminal o consola de comandos y ejecuta:
```bash
git clone https://github.com/nazahfr/tienda-patrones.git
cd tienda-patrones
```

---

#### C. Compilación del Proyecto

**Opción 1: Usando Apache Maven (Recomendada)**
```bash
mvn clean compile
```
Esto descargará las dependencias y compilará las 45 clases en la carpeta `target/classes`.

**Opción 2: Usando Java Compiler (`javac`) directamente**
* En Windows (PowerShell):
  ```powershell
  mkdir out -ErrorAction SilentlyContinue
  javac -encoding UTF-8 -d out (Get-ChildItem -Recurse -Filter *.java src/main | % FullName)
  ```
* En Linux / macOS / Git Bash:
  ```bash
  mkdir -p out
  javac -encoding UTF-8 -d out $(find src/main -name "*.java")
  ```

---

#### D. Modos de Ejecución

El proyecto ofrece 3 modalidades de ejecución según el requerimiento de evaluación:

##### 1. Interfaz Gráfica Interactiva (Java Swing - Recomendado para evaluación visual)
Permite probar interactivamente los 9 patrones mediante botones, paneles dedicados y una consola de salida en tiempo real.
* **Acceso Rápido en Windows:**
  Doble clic en el archivo:
  ```text
  iniciar-gui.bat
  ```
* **Desde Terminal con Java:**
  ```bash
  java -cp target/classes pe.utp.tienda.gui.DemoGUI
  ```
* **Desde Terminal con Maven:**
  ```bash
  mvn exec:java -Dexec.mainClass="pe.utp.tienda.gui.DemoGUI"
  ```

##### 2. Demostración en Consola (Recorrido técnico guiado)
Ejecuta la secuencia integral de los 9 patrones con trazabilidad de objetos:
```bash
mvn exec:java
# o directamente:
java -cp target/classes pe.utp.tienda.Demo
```

##### 3. Suite de Verificaciones y Pruebas Unitarias
Ejecuta 14 pruebas automatizadas con aserciones que validan el correcto funcionamiento de cada patrón:
```bash
java -cp target/classes pe.utp.tienda.Pruebas
```

---

## 🏛️ Matriz de Implementación de Patrones de Diseño

### 1. Patrones Creacionales (Semanas 6 y 7)
| Patrón | Clase / Paquete | Justificación y Aplicación |
| :--- | :--- | :--- |
| **Singleton** | `pe.utp.tienda.creational.singleton.ConfiguracionApp`<br>`pe.utp.tienda.creational.singleton.ConexionBD` | Punto de acceso global y único para la configuración general y la conexión a la base de datos MySQL. |
| **Prototype** | `pe.utp.tienda.model.Producto`<br>(`Polera`, `Pantalon`, `Zapatilla`) | Clonación de prendas base para generar variantes de talla y color manteniendo el stock independiente. |
| **Builder** | `pe.utp.tienda.model.Pedido.Builder` | Construcción paso a paso de pedidos complejos (cliente, ítems, método de pago, descuentos). |
| **Factory Method** | `pe.utp.tienda.creational.factory.ComprobanteFactory` | Desacoplamiento de la creación de comprobantes fiscales (`Boleta` o `Factura`) según el tipo de cliente. |
| **Abstract Factory** | `pe.utp.tienda.creational.abstractfactory.PasarelaPagoFactory`<br>(`YapeFactory`, `PlinFactory`, `TarjetaFactory`) | Creación de familias completas de procesamiento de pagos (procesador + tokenizador/validador). |

### 2. Patrones Estructurales (Semanas 8 y 9)
| Patrón | Clase / Paquete | Justificación y Aplicación |
| :--- | :--- | :--- |
| **Adapter** | `pe.utp.tienda.structural.adapter.ReniecAdapter` | Adapta la API legacy de RENIEC (parámetros enteros primitivos) a la interfaz del dominio (`String`, validación de 8 dígitos). |
| **Facade** | `pe.utp.tienda.structural.facade.VentaFacade` | Fachada unificada que orquesta el flujo de venta: validación de identidad, stock, cobro, comprobante y notificación. |
| **Decorator** | `pe.utp.tienda.structural.decorator.DecoradorPrecio`<br>(`ConDescuento`, `ConIgv`, `ConEnvio`) | Agrega dinámicamente modificaciones sobre el precio de las prendas (descuentos, impuestos, gastos de envío) en tiempo de ejecución. |
| **Composite** | `pe.utp.tienda.structural.composite.Categoria`<br>`pe.utp.tienda.model.Producto` | Estructuración en árbol de categorías y subcategorías, calculando de forma recursiva el stock total y valorización. |

---

## 📁 Estructura del Proyecto

```text
tienda-patrones/
├── docs/                           # Diagramas UML de cada patrón (.png y .dot)
├── src/main/java/pe/utp/tienda/
│   ├── creational/                 # Patrones Creacionales (Singleton, Prototype, Factory, Abstract Factory)
│   ├── structural/                 # Patrones Estructurales (Adapter, Facade, Decorator, Composite)
│   ├── model/                      # Entidades del dominio (Producto, Pedido, Cliente, Comprobantes)
│   ├── service/                    # Servicios de negocio (InventarioService, NotificacionService)
│   ├── gui/                        # Interfaz gráfica interactiva (DemoGUI.java)
│   ├── Demo.java                   # Demostración en consola
│   └── Pruebas.java                # Batería de pruebas automatizadas
├── iniciar-gui.bat                 # Script de inicio rápido en Windows
├── pom.xml                         # Configuración Maven (Java 17, exec-maven-plugin)
└── README.md                       # Manual de despliegue y documentación técnica
```

---

## 🗄️ Configuración de Base de Datos
* **Clase:** `ConexionBD.java`
* **Cadena de conexión:** `jdbc:mysql://localhost:3306/bd_tienda_ropa`
* **Credenciales:** Configurable mediante la variable de entorno `DB_PASSWORD`.
