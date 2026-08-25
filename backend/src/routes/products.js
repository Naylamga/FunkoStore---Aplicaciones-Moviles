const express = require('express');
const multer = require('multer');
const path = require('path');
const { supabase } = require('../supabase');
const config = require('../config');
const router = express.Router();
const upload = multer({
  storage: multer.memoryStorage(),
  limits: { fileSize: 8 * 1024 * 1024 }
});

const PRODUCT_SELECT = `
  id_producto,
  nombre_producto,
  descripcion,
  precio,
  stock,
  codigo_barra,
  id_categoria,
  id_franquicia,
  id_proveedor,
  estado,
  franquicias ( nombre_franquicia ),
  proveedores ( nombre_proveedor ),
  imagenes_producto ( id_imagen, id_producto, url_imagen, principal, orden_imagen )
`;

function mapProducto(row) {
  if (!row) return null;

  const imagenes = (row.imagenes_producto || [])
    .sort((a, b) => (a.orden_imagen || 0) - (b.orden_imagen || 0))
    .map((img) => ({
      id_imagen: img.id_imagen,
      id_producto: img.id_producto,
      url_imagen: img.url_imagen,
      principal: !!img.principal,
      orden_imagen: img.orden_imagen
    }));

  return {
    id_producto: row.id_producto,
    nombre_producto: row.nombre_producto,
    descripcion: row.descripcion,
    precio: Number(row.precio),
    stock: row.stock ?? 0,
    codigo_barra: row.codigo_barra,
    id_franquicia: row.id_franquicia,
    id_proveedor: row.id_proveedor,
    estado: row.estado !== false,
    nombre_franquicia: row.franquicias?.nombre_franquicia || null,
    nombre_categoria: null,
    nombre_proveedor: row.proveedores?.nombre_proveedor || null,
    imagenes
  };
}

async function fetchProductoById(id) {
  const { data, error } = await supabase
    .from('productos')
    .select(PRODUCT_SELECT)
    .eq('id_producto', id)
    .maybeSingle();

  if (error) throw error;
  return mapProducto(data);
}

async function uploadImages(idProducto, files) {
  if (!files || files.length === 0) return [];

  const uploaded = [];
  for (let i = 0; i < files.length; i++) {
    const file = files[i];
    const ext = path.extname(file.originalname || '') || '.jpg';
    const objectPath = `producto_${idProducto}/${Date.now()}_${i}${ext}`;

    const { error: upError } = await supabase.storage
      .from(config.bucket)
      .upload(objectPath, file.buffer, {
        contentType: file.mimetype || 'image/jpeg',
        upsert: false
      });

    if (upError) {
      console.error('upload image', upError);
      throw new Error(`Error al subir imagen: ${upError.message}`);
    }

    const { data: publicData } = supabase.storage
      .from(config.bucket)
      .getPublicUrl(objectPath);

    uploaded.push({
      id_producto: idProducto,
      url_imagen: publicData.publicUrl,
      principal: i === 0,
      orden_imagen: i
    });
  }

  const { error: insertError } = await supabase
    .from('imagenes_producto')
    .insert(uploaded);

  if (insertError) throw insertError;
  return uploaded;
}

function parseBodyFields(body) {
  const toInt = (v) => {
    if (v === undefined || v === null || v === '') return null;
    const n = parseInt(String(v), 10);
    return Number.isNaN(n) ? null : n;
  };
  const toFloat = (v) => {
    if (v === undefined || v === null || v === '') return null;
    const n = parseFloat(String(v));
    return Number.isNaN(n) ? null : n;
  };

  return {
    nombre_producto: (body.nombre_producto || '').trim(),
    descripcion: body.descripcion ? String(body.descripcion).trim() : null,
    precio: toFloat(body.precio),
    stock: toInt(body.stock) ?? 0,
    codigo_barra: body.codigo_barra ? String(body.codigo_barra).trim() : null,
    id_franquicia: toInt(body.id_franquicia),
    id_proveedor: toInt(body.id_proveedor),
    id_categoria: toInt(body.id_categoria)
  };
}

router.get('/', async (req, res) => {
  try {
    const page = Math.max(1, parseInt(req.query.page, 10) || 1);
    const limit = Math.min(100, Math.max(1, parseInt(req.query.limit, 10) || 100));
    const from = (page - 1) * limit;
    const to = from + limit - 1;

    let query = supabase
      .from('productos')
      .select(PRODUCT_SELECT, { count: 'exact' })
      .eq('estado', true)
      .order('id_producto', { ascending: false })
      .range(from, to);

    if (req.query.search) {
      query = query.ilike('nombre_producto', `%${req.query.search}%`);
    }
    if (req.query.franquicia) {
      query = query.eq('id_franquicia', parseInt(req.query.franquicia, 10));
    }
    if (req.query.categoria) {
      // filtrar por categoría vía id_categoria del producto si existe
      query = query.eq('id_categoria', parseInt(req.query.categoria, 10));
    }

    const { data, error, count } = await query;
    if (error) throw error;

    const total = count ?? 0;
    const totalPages = Math.max(1, Math.ceil(total / limit));

    return res.json({
      success: true,
      data: (data || []).map(mapProducto),
      total,
      page,
      limit,
      totalPages
    });
  } catch (err) {
    console.error('list products', err);
    return res.status(500).json({
      success: false,
      message: err.message || 'Error al listar productos'
    });
  }
});

router.get('/:id', async (req, res) => {
  try {
    const producto = await fetchProductoById(req.params.id);
    if (!producto) {
      return res.status(404).json({
        success: false,
        message: 'Producto no encontrado'
      });
    }
    return res.json({ success: true, data: producto });
  } catch (err) {
    console.error('get product', err);
    return res.status(500).json({
      success: false,
      message: err.message || 'Error al obtener producto'
    });
  }
});

router.post('/', upload.array('imagenes', 5), async (req, res) => {
  try {
    const fields = parseBodyFields(req.body);

    if (!fields.nombre_producto || fields.precio == null) {
      return res.status(400).json({
        success: false,
        message: 'nombre_producto y precio son obligatorios'
      });
    }

    if (fields.id_franquicia && !fields.id_categoria) {
      const { data: franq } = await supabase
        .from('franquicias')
        .select('id_categoria')
        .eq('id_franquicia', fields.id_franquicia)
        .maybeSingle();
      fields.id_categoria = franq?.id_categoria ?? null;
    }

    const { data: insertado, error } = await supabase
      .from('productos')
      .insert({
        nombre_producto: fields.nombre_producto,
        descripcion: fields.descripcion,
        precio: fields.precio,
        stock: fields.stock,
        codigo_barra: fields.codigo_barra,
        id_franquicia: fields.id_franquicia,
        id_proveedor: fields.id_proveedor,
        id_categoria: fields.id_categoria,
        estado: true
      })
      .select('id_producto')
      .single();

    if (error) throw error;

    await uploadImages(insertado.id_producto, req.files);

    const producto = await fetchProductoById(insertado.id_producto);

    const io = req.app.get('io');
    if (io) {
      io.emit('stock:updated', { productoId: insertado.id_producto });
    }

    return res.status(201).json({
      success: true,
      message: 'Producto creado',
      data: producto
    });
  } catch (err) {
    console.error('create product', err);
    return res.status(500).json({
      success: false,
      message: err.message || 'Error al crear producto'
    });
  }
});

router.put('/:id', upload.array('imagenes', 5), async (req, res) => {
  try {
    const id = parseInt(req.params.id, 10);
    const fields = parseBodyFields(req.body);

    if (!fields.nombre_producto || fields.precio == null) {
      return res.status(400).json({
        success: false,
        message: 'nombre_producto y precio son obligatorios'
      });
    }

    if (fields.id_franquicia && !fields.id_categoria) {
      const { data: franq } = await supabase
        .from('franquicias')
        .select('id_categoria')
        .eq('id_franquicia', fields.id_franquicia)
        .maybeSingle();
      fields.id_categoria = franq?.id_categoria ?? null;
    }

    const { error } = await supabase
      .from('productos')
      .update({
        nombre_producto: fields.nombre_producto,
        descripcion: fields.descripcion,
        precio: fields.precio,
        stock: fields.stock,
        codigo_barra: fields.codigo_barra,
        id_franquicia: fields.id_franquicia,
        id_proveedor: fields.id_proveedor,
        id_categoria: fields.id_categoria,
        fecha_actualizacion: new Date().toISOString()
      })
      .eq('id_producto', id);

    if (error) throw error;

    if (req.files && req.files.length > 0) {
      await uploadImages(id, req.files);
    }

    const producto = await fetchProductoById(id);
    if (!producto) {
      return res.status(404).json({
        success: false,
        message: 'Producto no encontrado'
      });
    }

    const io = req.app.get('io');
    if (io) {
      io.emit('stock:updated', { productoId: id });
    }

    return res.json({
      success: true,
      message: 'Producto actualizado',
      data: producto
    });
  } catch (err) {
    console.error('update product', err);
    return res.status(500).json({
      success: false,
      message: err.message || 'Error al actualizar producto'
    });
  }
});

router.delete('/:id', async (req, res) => {
  try {
    const id = parseInt(req.params.id, 10);
    const { error } = await supabase
      .from('productos')
      .update({
        estado: false,
        fecha_actualizacion: new Date().toISOString()
      })
      .eq('id_producto', id);

    if (error) throw error;

    return res.json({
      success: true,
      message: 'Producto dado de baja',
      data: null
    });
  } catch (err) {
    console.error('delete product', err);
    return res.status(500).json({
      success: false,
      message: err.message || 'Error al eliminar producto'
    });
  }
});

module.exports = router;
