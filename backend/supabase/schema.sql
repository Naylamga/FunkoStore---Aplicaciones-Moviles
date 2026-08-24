-- Ejecutar en Supabase → SQL Editor → Run
-- Tabla simple para la actividad (alta de producto)

CREATE TABLE IF NOT EXISTS inventario (
    id SERIAL PRIMARY KEY,
    producto TEXT NOT NULL,
    marca TEXT NOT NULL,
    descripcion TEXT NOT NULL,
    creado_en TIMESTAMPTZ DEFAULT NOW()
);
