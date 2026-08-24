require('dotenv').config();
const ws = require('ws');
const express = require('express');
const cors = require('cors');
const { createClient } = require('@supabase/supabase-js');

const PORT = Number(process.env.PORT) || 3000;
const SUPABASE_URL = process.env.SUPABASE_URL;
const SUPABASE_KEY = process.env.SUPABASE_SERVICE_ROLE_KEY;

if (!SUPABASE_URL || !SUPABASE_KEY) {
  console.error('Falta SUPABASE_URL o SUPABASE_SERVICE_ROLE_KEY en backend/.env');
  process.exit(1);
}

const supabase = createClient(SUPABASE_URL, SUPABASE_KEY, {
  auth: { autoRefreshToken: false, persistSession: false },
  realtime: { transport: ws }
});

const app = express();
app.use(cors());
app.use(express.json());

app.get('/', (_req, res) => {
  res.json({
    ok: true,
    mensaje: 'API FunkoStore (simple)',
    endpoint: 'POST /productos'
  });
});

/**
 * Alta de producto — igual que la guía:
 * Body: { producto, marca, descripcion }
 * Respuesta: { mensaje } o { error }
 */
app.post('/productos', async (req, res) => {
  try {
    const producto = (req.body.producto || '').trim();
    const marca = (req.body.marca || '').trim();
    const descripcion = (req.body.descripcion || '').trim();

    if (!producto || !marca || !descripcion) {
      return res.status(400).json({
        error: 'producto, marca y descripcion son obligatorios'
      });
    }

    const { error } = await supabase.from('inventario').insert({
      producto,
      marca,
      descripcion
    });

    if (error) {
      console.error(error);
      return res.status(500).json({ error: error.message });
    }

    return res.status(201).json({
      mensaje: 'Producto creado correctamente'
    });
  } catch (err) {
    console.error(err);
    return res.status(500).json({
      error: err.message || 'Error al crear producto'
    });
  }
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`API en http://localhost:${PORT}`);
  console.log(`Emulador Android: http://10.0.2.2:${PORT}/productos`);
});
