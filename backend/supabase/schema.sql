-- Ejecutar en Supabase: SQL Editor → New query → Run
-- Esquema de inventario (adaptado de funko_house MySQL → Postgres)

CREATE TABLE IF NOT EXISTS roles (
    id_rol SERIAL PRIMARY KEY,
    nombre_rol VARCHAR(50),
    descripcion VARCHAR(255),
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMPTZ DEFAULT NOW(),
    fecha_actualizacion TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    email VARCHAR(150) UNIQUE,
    contrasena VARCHAR(255),
    telefono VARCHAR(30),
    direccion VARCHAR(255),
    fecha_nacimiento DATE,
    id_rol INT REFERENCES roles(id_rol),
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMPTZ DEFAULT NOW(),
    fecha_actualizacion TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS categorias (
    id_categoria SERIAL PRIMARY KEY,
    nombre_categoria VARCHAR(100),
    descripcion VARCHAR(255),
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMPTZ DEFAULT NOW(),
    fecha_actualizacion TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS franquicias (
    id_franquicia SERIAL PRIMARY KEY,
    nombre_franquicia VARCHAR(100),
    descripcion VARCHAR(255),
    id_categoria INT REFERENCES categorias(id_categoria),
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMPTZ DEFAULT NOW(),
    fecha_actualizacion TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS proveedores (
    id_proveedor SERIAL PRIMARY KEY,
    nombre_proveedor VARCHAR(150),
    cuit VARCHAR(20),
    telefono VARCHAR(30),
    email VARCHAR(150),
    direccion VARCHAR(255),
    contacto VARCHAR(100),
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMPTZ DEFAULT NOW(),
    fecha_actualizacion TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS productos (
    id_producto SERIAL PRIMARY KEY,
    nombre_producto VARCHAR(150),
    descripcion TEXT,
    precio NUMERIC(10,2),
    stock INT DEFAULT 0,
    codigo_barra VARCHAR(100),
    id_categoria INT REFERENCES categorias(id_categoria),
    id_franquicia INT REFERENCES franquicias(id_franquicia),
    id_proveedor INT REFERENCES proveedores(id_proveedor),
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMPTZ DEFAULT NOW(),
    fecha_actualizacion TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS imagenes_producto (
    id_imagen SERIAL PRIMARY KEY,
    id_producto INT REFERENCES productos(id_producto) ON DELETE CASCADE,
    url_imagen VARCHAR(500),
    principal BOOLEAN DEFAULT FALSE,
    orden_imagen INT DEFAULT 0
);

-- Datos iniciales
INSERT INTO roles (nombre_rol) VALUES
('Cliente'),
('Administrador'),
('SuperAdmin')
ON CONFLICT DO NOTHING;

INSERT INTO categorias (nombre_categoria, descripcion)
SELECT 'Anime', 'Figuras de anime y manga'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre_categoria = 'Anime');

INSERT INTO categorias (nombre_categoria, descripcion)
SELECT 'Películas', 'Figuras de cine y series'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre_categoria = 'Películas');

INSERT INTO franquicias (nombre_franquicia, descripcion, id_categoria)
SELECT 'Naruto', 'Universo Naruto', c.id_categoria
FROM categorias c WHERE c.nombre_categoria = 'Anime'
AND NOT EXISTS (SELECT 1 FROM franquicias WHERE nombre_franquicia = 'Naruto');

INSERT INTO franquicias (nombre_franquicia, descripcion, id_categoria)
SELECT 'Marvel', 'Universo Marvel', c.id_categoria
FROM categorias c WHERE c.nombre_categoria = 'Películas'
AND NOT EXISTS (SELECT 1 FROM franquicias WHERE nombre_franquicia = 'Marvel');

INSERT INTO proveedores (nombre_proveedor, cuit, email, contacto)
SELECT 'Funko Official', '30-00000000-0', 'ventas@funko.example', 'Distribuidor principal'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE nombre_proveedor = 'Funko Official');

INSERT INTO proveedores (nombre_proveedor, cuit, email, contacto)
SELECT 'Import Toys', '30-11111111-1', 'contacto@importoys.example', 'Importador local'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE nombre_proveedor = 'Import Toys');

-- Bucket de imágenes: crear en Dashboard → Storage → New bucket
-- Nombre: productos
-- Public bucket: sí (para que la app lea las URLs)
