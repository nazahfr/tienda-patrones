-- Esquema minimo que usan los DAO JDBC (ProductoDAOJdbc y PedidoDAOJdbc).
-- IMPORTANTE: es el esquema que necesita el codigo de la PC3. Si tu bd_tienda_ropa de la PC1 ya existe con otras
-- columnas, ajusta los nombres en los DAO o crea estas tablas en una base nueva.
CREATE DATABASE IF NOT EXISTS bd_tienda_ropa CHARACTER SET utf8mb4;
USE bd_tienda_ropa;

CREATE TABLE IF NOT EXISTS PRODUCTO (
    sku          VARCHAR(20)  PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    tipo         VARCHAR(20)  NOT NULL,          -- Polera | Pantalon | Zapatilla
    talla        VARCHAR(10)  NOT NULL,
    precio       DECIMAL(10,2) NOT NULL,
    stock        INT NOT NULL,
    stock_minimo INT NOT NULL DEFAULT 5
);

CREATE TABLE IF NOT EXISTS CLIENTE (
    dni       CHAR(8)      PRIMARY KEY,
    nombres   VARCHAR(80)  NOT NULL,
    apellidos VARCHAR(80)  NOT NULL
);

CREATE TABLE IF NOT EXISTS PEDIDO (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    dni_cliente    CHAR(8)      NOT NULL,
    estado         VARCHAR(20)  NOT NULL,        -- Pendiente | Pagado | En Preparacion | Enviado | Entregado | Cancelado
    descuento_pct  DECIMAL(5,2) NOT NULL DEFAULT 0,
    costo_envio    DECIMAL(10,2) NOT NULL DEFAULT 0,
    notas          VARCHAR(255),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dni_cliente) REFERENCES CLIENTE(dni)
);

CREATE TABLE IF NOT EXISTS DETALLE_PEDIDO (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido  INT         NOT NULL,
    sku        VARCHAR(20) NOT NULL,
    cantidad   INT         NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES PEDIDO(id),
    FOREIGN KEY (sku) REFERENCES PRODUCTO(sku)
);

INSERT IGNORE INTO PRODUCTO (sku, nombre, tipo, talla, precio, stock, stock_minimo) VALUES
    ('POL-OVR-001', 'Polera Oversize Negra',        'Polera',    'M',  59.00, 25, 5),
    ('PAN-CRG-001', 'Pantalon Cargo Beige',         'Pantalon',  '32', 79.00, 15, 5),
    ('ZAP-URB-001', 'Zapatillas Urbanas Blancas',   'Zapatilla', '40', 148.00, 10, 5);
