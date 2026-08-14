const express = require('express');
const bcrypt = require('bcryptjs');
const { supabase } = require('../supabase');
const { authRequired, signToken } = require('../middleware/auth');

const router = express.Router();

function passwordFromBody(body) {
  return body.contraseña ?? body.contrasena ?? body.password ?? '';
}

function mapUsuario(row, rolNombre) {
  return {
    id: row.id_usuario,
    nombre: row.nombre,
    apellido: row.apellido,
    email: row.email,
    rol: rolNombre || null
  };
}

router.post('/login', async (req, res) => {
  try {
    const email = (req.body.email || '').trim().toLowerCase();
    const password = passwordFromBody(req.body);

    if (!email || !password) {
      return res.status(400).json({
        success: false,
        message: 'Email y contraseña son obligatorios'
      });
    }

    const { data: usuario, error } = await supabase
      .from('usuarios')
      .select('*, roles(nombre_rol)')
      .eq('email', email)
      .eq('estado', true)
      .maybeSingle();

    if (error) throw error;

    if (!usuario) {
      return res.status(401).json({
        success: false,
        message: 'Email o contraseña incorrectos'
      });
    }

    const ok = await bcrypt.compare(password, usuario.contrasena || '');
    if (!ok) {
      return res.status(401).json({
        success: false,
        message: 'Email o contraseña incorrectos'
      });
    }

    const rol = usuario.roles?.nombre_rol || null;
    const token = signToken({
      id: usuario.id_usuario,
      email: usuario.email,
      rol
    });

    return res.json({
      success: true,
      message: 'Login exitoso',
      data: {
        token,
        usuario: mapUsuario(usuario, rol)
      }
    });
  } catch (err) {
    console.error('login', err);
    return res.status(500).json({
      success: false,
      message: err.message || 'Error en login'
    });
  }
});

router.post('/register', async (req, res) => {
  try {
    const nombre = (req.body.nombre || '').trim();
    const apellido = (req.body.apellido || '').trim();
    const email = (req.body.email || '').trim().toLowerCase();
    const password = passwordFromBody(req.body);
    const telefono = req.body.telefono || null;
    const direccion = req.body.direccion || null;

    if (!nombre || !apellido || !email || !password) {
      return res.status(400).json({
        success: false,
        message: 'Nombre, apellido, email y contraseña son obligatorios'
      });
    }

    const { data: existente } = await supabase
      .from('usuarios')
      .select('id_usuario')
      .eq('email', email)
      .maybeSingle();

    if (existente) {
      return res.status(409).json({
        success: false,
        message: 'El email ya está registrado'
      });
    }

    const { data: rolCliente } = await supabase
      .from('roles')
      .select('id_rol, nombre_rol')
      .eq('nombre_rol', 'Cliente')
      .maybeSingle();

    const hash = await bcrypt.hash(password, 10);

    const { data: usuario, error } = await supabase
      .from('usuarios')
      .insert({
        nombre,
        apellido,
        email,
        contrasena: hash,
        telefono,
        direccion,
        id_rol: rolCliente?.id_rol ?? 1
      })
      .select('*')
      .single();

    if (error) throw error;

    const rol = rolCliente?.nombre_rol || 'Cliente';
    const token = signToken({
      id: usuario.id_usuario,
      email: usuario.email,
      rol
    });

    return res.status(201).json({
      success: true,
      message: 'Cuenta creada',
      data: {
        token,
        usuario: mapUsuario(usuario, rol)
      }
    });
  } catch (err) {
    console.error('register', err);
    return res.status(500).json({
      success: false,
      message: err.message || 'Error al registrar'
    });
  }
});

router.get('/profile', authRequired, async (req, res) => {
  try {
    const { data: usuario, error } = await supabase
      .from('usuarios')
      .select('id_usuario, nombre, apellido, email, telefono, direccion, roles(nombre_rol)')
      .eq('id_usuario', req.user.id)
      .maybeSingle();

    if (error) throw error;
    if (!usuario) {
      return res.status(404).json({
        success: false,
        message: 'Usuario no encontrado'
      });
    }

    return res.json({
      success: true,
      data: {
        id_usuario: usuario.id_usuario,
        nombre: usuario.nombre,
        apellido: usuario.apellido,
        email: usuario.email,
        telefono: usuario.telefono,
        direccion: usuario.direccion,
        nombre_rol: usuario.roles?.nombre_rol || null
      }
    });
  } catch (err) {
    console.error('profile', err);
    return res.status(500).json({
      success: false,
      message: err.message || 'Error al obtener perfil'
    });
  }
});

router.put('/profile', authRequired, async (req, res) => {
  try {
    const updates = {};
    if (req.body.nombre != null) updates.nombre = req.body.nombre;
    if (req.body.apellido != null) updates.apellido = req.body.apellido;
    if (req.body.telefono != null) updates.telefono = req.body.telefono;
    if (req.body.direccion != null) updates.direccion = req.body.direccion;
    updates.fecha_actualizacion = new Date().toISOString();

    const { data: usuario, error } = await supabase
      .from('usuarios')
      .update(updates)
      .eq('id_usuario', req.user.id)
      .select('id_usuario, nombre, apellido, email, telefono, direccion, roles(nombre_rol)')
      .single();

    if (error) throw error;

    return res.json({
      success: true,
      message: 'Perfil actualizado',
      data: {
        id_usuario: usuario.id_usuario,
        nombre: usuario.nombre,
        apellido: usuario.apellido,
        email: usuario.email,
        telefono: usuario.telefono,
        direccion: usuario.direccion,
        nombre_rol: usuario.roles?.nombre_rol || null
      }
    });
  } catch (err) {
    console.error('update profile', err);
    return res.status(500).json({
      success: false,
      message: err.message || 'Error al actualizar perfil'
    });
  }
});

module.exports = router;
