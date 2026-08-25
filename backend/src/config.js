require('dotenv').config();

const required = ['SUPABASE_URL', 'SUPABASE_SERVICE_ROLE_KEY', 'JWT_SECRET'];

for (const key of required) {
  if (!process.env[key]) {
    console.warn(`[config] Falta variable de entorno: ${key}`);
  }
}

module.exports = {
  port: Number(process.env.PORT) || 3000,
  jwtSecret: process.env.JWT_SECRET || 'dev-secret',
  supabaseUrl: process.env.SUPABASE_URL,
  supabaseServiceKey: process.env.SUPABASE_SERVICE_ROLE_KEY,
  bucket: process.env.SUPABASE_BUCKET || 'productos'
};
