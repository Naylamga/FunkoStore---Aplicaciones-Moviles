const express = require('express');
const { supabase } = require('../supabase');
const { authRequired } = require('../middleware/auth');

const router = express.Router();

router.get('/', authRequired, async (req, res) => {
  try {
    const { data, error } = await supabase
      .from('franquicias')
      .select('id_franquicia, nombre_franquicia, descripcion')
      .eq('estado', true)
      .order('nombre_franquicia');

    if (error) throw error;

    return res.json({
      success: true,
      data: data || []
    });
  } catch (err) {
    console.error('franchises', err);
    return res.status(500).json({
      success: false,
      message: err.message || 'Error al obtener franquicias'
    });
  }
});

module.exports = router;
