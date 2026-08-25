const http = require('http');
const express = require('express');
const cors = require('cors');
const { Server } = require('socket.io');
const config = require('./config');

const authRoutes = require('./routes/auth');
const productRoutes = require('./routes/products');
const franchiseRoutes = require('./routes/franchises');
const providerRoutes = require('./routes/providers');

const app = express();
const server = http.createServer(app);

const io = new Server(server, {
  cors: { origin: '*' }
});
app.set('io', io);

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.get('/', (_req, res) => {
  res.json({
    success: true,
    message: 'FunkoStore API (Node + Supabase)',
    routes: [
      'POST /api/auth/login',
      'POST /api/auth/register',
      'GET  /api/products',
      'POST /api/products',
      'GET  /api/franchises',
      'GET  /api/providers'
    ]
  });
});

app.get('/api/health', (_req, res) => {
  res.json({ success: true, message: 'ok' });
});

app.use('/api/auth', authRoutes);
app.use('/api/products', productRoutes);
app.use('/api/franchises', franchiseRoutes);
app.use('/api/providers', providerRoutes);

app.use((err, _req, res, _next) => {
  console.error(err);
  res.status(500).json({
    success: false,
    message: err.message || 'Error interno'
  });
});

io.on('connection', (socket) => {
  console.log('Socket conectado:', socket.id);
  socket.on('disconnect', () => {
    console.log('Socket desconectado:', socket.id);
  });
});

server.listen(config.port, '0.0.0.0', () => {
  console.log(`API FunkoStore en http://0.0.0.0:${config.port}`);
  console.log(`App Android (emulador): http://10.0.2.2:${config.port}/api/`);
});
