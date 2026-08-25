const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const { supabase } = require('../supabase');
const config = require('../config');

const router = express.Router();

function signToken(payload) {
  return jwt.sign(payload, config.jwtSecret, { expiresIn: '7d' });
}

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

module.exports = router;
