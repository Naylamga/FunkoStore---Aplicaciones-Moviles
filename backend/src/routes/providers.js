const express = require('express');
const { supabase } = require('../supabase');
const { authRequired } = require('../middleware/auth');

const router = express.Router();

router.get('/', authRequired, async (req, res) => {
  try {
    const { data, error } = await supabase
      .from('proveedores')
      .select('id_proveedor, nombre_proveedor, cuit, telefono, email')
      .eq('estado', true)
      .order('nombre_proveedor');

    if (error) throw error;

    return res.json({
      success: true,
      data: data || []
    });
  } catch (err) {
    console.error('providers', err);
    return res.status(500).json({
      success: false,
      message: err.message || 'Error al obtener proveedores'
    });
  }
});

module.exports = router;
