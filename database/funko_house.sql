CREATE DATABASE IF NOT EXISTS funko_house;
USE funko_house;

CREATE TABLE roles (
    id_rol INT PRIMARY KEY AUTO_INCREMENT,
    nombre_rol VARCHAR(50),
    descripcion VARCHAR(255),
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE usuarios (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    email VARCHAR(150) UNIQUE,
    contraseña VARCHAR(255),
    telefono VARCHAR(30),
    direccion VARCHAR(255),
    fecha_nacimiento DATE,
    id_rol INT,
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

CREATE TABLE categorias (
    id_categoria INT PRIMARY KEY AUTO_INCREMENT,
    nombre_categoria VARCHAR(100),
    descripcion VARCHAR(255),
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE franquicias (
    id_franquicia INT PRIMARY KEY AUTO_INCREMENT,
    nombre_franquicia VARCHAR(100),
    descripcion VARCHAR(255),
    id_categoria INT,
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
);

CREATE TABLE proveedores (
    id_proveedor INT PRIMARY KEY AUTO_INCREMENT,
    nombre_proveedor VARCHAR(150),
    cuit VARCHAR(20),
    telefono VARCHAR(30),
    email VARCHAR(150),
    direccion VARCHAR(255),
    contacto VARCHAR(100),
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE productos (
    id_producto INT PRIMARY KEY AUTO_INCREMENT,
    nombre_producto VARCHAR(150),
    descripcion TEXT,
    precio DECIMAL(10,2),
    stock INT,
    codigo_barra VARCHAR(100),
    id_categoria INT,
    id_franquicia INT,
    id_proveedor INT,
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria),
    FOREIGN KEY (id_franquicia) REFERENCES franquicias(id_franquicia),
    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor)
);

CREATE TABLE imagenes_producto (
    id_imagen INT PRIMARY KEY AUTO_INCREMENT,
    id_producto INT,
    url_imagen VARCHAR(255),
    principal BOOLEAN DEFAULT FALSE,
    orden_imagen INT,

    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

CREATE TABLE favoritos (
    id_favorito INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT,
    id_producto INT,
    fecha_agregado DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

CREATE TABLE carrito (
    id_carrito INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE detalle_carrito (
    id_detalle_carrito INT PRIMARY KEY AUTO_INCREMENT,
    id_carrito INT,
    id_producto INT,
    cantidad INT,
    subtotal DECIMAL(10,2),

    FOREIGN KEY (id_carrito) REFERENCES carrito(id_carrito),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

CREATE TABLE pedidos (
    id_pedido INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT,
    fecha_pedido DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado_pedido VARCHAR(50),
    total DECIMAL(10,2),
    direccion_envio VARCHAR(255),
    observaciones TEXT,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE detalle_pedidos (
    id_detalle_pedido INT PRIMARY KEY AUTO_INCREMENT,
    id_pedido INT,
    id_producto INT,
    cantidad INT,
    precio_unitario DECIMAL(10,2),
    subtotal DECIMAL(10,2),

    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

CREATE TABLE pagos (
    id_pago INT PRIMARY KEY AUTO_INCREMENT,
    id_pedido INT,
    metodo_pago VARCHAR(50),
    estado_pago VARCHAR(50),
    fecha_pago DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
);

CREATE TABLE envios (
    id_envio INT PRIMARY KEY AUTO_INCREMENT,
    id_pedido INT,
    empresa_envio VARCHAR(100),
    codigo_seguimiento VARCHAR(100),
    estado_envio VARCHAR(50),
    fecha_envio DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
);

CREATE TABLE compra (
    id_compra INT PRIMARY KEY AUTO_INCREMENT,
    id_proveedor INT,
    fecha_compra DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_compra DECIMAL(10,2),
    metodo_pago VARCHAR(50),
    observaciones TEXT,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor)
);

CREATE TABLE detalle_compra (
    id_detalle_compra INT PRIMARY KEY AUTO_INCREMENT,
    id_compra INT,
    id_producto INT,
    cantidad INT,
    precio_unitario DECIMAL(10,2),
    subtotal DECIMAL(10,2),

    FOREIGN KEY (id_compra) REFERENCES compra(id_compra),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

CREATE TABLE factura (
    id_factura INT PRIMARY KEY AUTO_INCREMENT,
    id_pedido INT,
    fecha_factura DATETIME DEFAULT CURRENT_TIMESTAMP,
    tipo_factura VARCHAR(10),
    numero_factura VARCHAR(50),
    subtotal DECIMAL(10,2),
    iva DECIMAL(10,2),
    total_factura DECIMAL(10,2),
    metodo_pago VARCHAR(50),
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
);

CREATE TABLE detalle_factura (
    id_detalle_factura INT PRIMARY KEY AUTO_INCREMENT,
    id_factura INT,
    id_producto INT,
    cantidad INT,
    precio_unitario DECIMAL(10,2),
    subtotal DECIMAL(10,2),

    FOREIGN KEY (id_factura) REFERENCES factura(id_factura),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

CREATE TABLE movimientos_stock (
    id_movimiento INT PRIMARY KEY AUTO_INCREMENT,
    id_producto INT,
    tipo_movimiento VARCHAR(50),
    cantidad INT,
    descripcion TEXT,
    fecha_movimiento DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

CREATE TABLE reseñas (
    id_reseña INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT,
    id_producto INT,
    puntuacion INT,
    comentario TEXT,
    fecha_reseña DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

CREATE TABLE cupones (
    id_cupon INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(50),
    tipo_descuento VARCHAR(50),
    descuento DECIMAL(10,2),
    fecha_inicio DATE,
    fecha_fin DATE,
    estado BOOLEAN DEFAULT TRUE
);

CREATE TABLE pedido_cupon (
    id_pedido_cupon INT PRIMARY KEY AUTO_INCREMENT,
    id_pedido INT,
    id_cupon INT,

    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido),
    FOREIGN KEY (id_cupon) REFERENCES cupones(id_cupon)
);

INSERT INTO roles (nombre_rol)
VALUES
('Cliente'),
('Administrador'),
('SuperAdmin');