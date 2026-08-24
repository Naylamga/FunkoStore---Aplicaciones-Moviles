# FunkoStore — Alta de producto (API + Supabase)

App Android simple (como la guía) + API Node.js + base de datos en Supabase.

```
App Android  →  POST http://IP:PUERTO/productos  →  Node.js  →  Supabase
```

## 1. Supabase (una sola vez)

1. Creá un proyecto en [supabase.com](https://supabase.com)
2. Andá a **SQL Editor** → New query → pegá y ejecutá esto:

```sql
CREATE TABLE IF NOT EXISTS inventario (
    id SERIAL PRIMARY KEY,
    producto TEXT NOT NULL,
    marca TEXT NOT NULL,
    descripcion TEXT NOT NULL,
    creado_en TIMESTAMPTZ DEFAULT NOW()
);
```

(También está en `backend/supabase/schema.sql`)

3. Andá a **Project Settings → API Keys** y copiá:
   - **Project URL** → ejemplo: `https://xxxx.supabase.co` (sin `/rest/v1/`)
   - **Secret key** (`sb_secret_...` o la legacy `service_role`)

## 2. API Node.js

```bash
cd backend
copy .env.example .env
```

Editá `backend/.env`:

```env
PORT=3000
SUPABASE_URL=https://TU_PROYECTO.supabase.co
SUPABASE_SERVICE_ROLE_KEY=tu_secret_key
```

Instalá y arrancá:

```bash
npm install
npm run dev
```

Deberías ver:

```
API en http://localhost:3000
Emulador Android: http://10.0.2.2:3000/productos
```

Probá en el navegador: [http://localhost:3000](http://localhost:3000)

## 3. App Android

1. Abrí `FunkoStore-App` en Android Studio
2. Corré en el **emulador**
3. En la pantalla de configuración usá (valores por defecto):

| Campo | Valor (emulador) |
|--------|-------------------|
| IP | `10.0.2.2` |
| Puerto | `3000` |
| Endpoint | `productos` |

> `10.0.2.2` = `localhost` de tu PC visto desde el emulador.

4. Continuá → completá producto, marca y descripción → **Enviar producto**
5. Revisá en Supabase → **Table Editor** → tabla `inventario`

### Si usás celular físico

- Poné la IP de tu PC en la red WiFi (ej. `192.168.1.10`)
- PC y celular en la misma WiFi
- Firewall de Windows: permitir Node en el puerto 3000

## Estructura

```
backend/
  src/index.js              # API: POST /productos
  supabase/schema.sql       # Tabla inventario
  .env.example
FunkoStore-App/             # App Android (config + alta)
database/funko_house.sql    # BD original de la actividad (referencia)
```

## Endpoint

| Método | Ruta | Body JSON | Respuesta OK |
|--------|------|-----------|--------------|
| POST | `/productos` | `{ "producto", "marca", "descripcion" }` | `{ "mensaje": "Producto creado correctamente" }` |

## Problemas frecuentes

| Problema | Solución |
|----------|----------|
| No se conecta la app | La API tiene que estar corriendo (`npm run dev`) |
| Emulador | IP `10.0.2.2`, no `localhost` |
| Error de Supabase | Revisá URL y secret key en `.env` |
| Tabla no existe | Ejecutá el SQL del paso 1 |
| Cleartext / HTTP | Ya está permitido en el Manifest |
