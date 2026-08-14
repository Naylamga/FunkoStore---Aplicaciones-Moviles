const { createClient } = require('@supabase/supabase-js');
const ws = require('ws');
const config = require('./config');

if (!config.supabaseUrl || !config.supabaseServiceKey) {
  console.error('Configurá SUPABASE_URL y SUPABASE_SERVICE_ROLE_KEY en backend/.env');
}

const supabase = createClient(
  config.supabaseUrl || '',
  config.supabaseServiceKey || '',
  {
    auth: {
      autoRefreshToken: false,
      persistSession: false
    },
    realtime: {
      // Node < 22 no trae WebSocket nativo
      transport: ws
    }
  }
);

module.exports = { supabase };
