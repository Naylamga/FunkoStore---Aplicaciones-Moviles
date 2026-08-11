# FunkoStore — Aplicaciones Móviles

App Android de gestión de productos + API Node.js con Supabase.

## Estructura

- `FunkoStore-App/` — Android (Retrofit → `http://10.0.2.2:3000/api/`)
- `backend/` — API Express + Socket.IO + Supabase
- `database/funko_house.sql` — esquema original MySQL (referencia)
- `backend/supabase/schema.sql` — esquema Postgres para Supabase

## Setup rápido (Node + Supabase)

1. Crear proyecto en [supabase.com](https://supabase.com)
2. SQL Editor: correr `backend/supabase/schema.sql`
3. Storage → New bucket `productos` → **Public**
4. Project Settings → API: copiar URL y **service_role** key
5. En `backend/`:

```bash
cd backend
copy .env.example .env
# editar .env con SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY, JWT_SECRET
npm install
npm run dev
```

6. Emulador Android: la app ya apunta a `10.0.2.2:3000` (localhost de tu PC).

## Endpoints usados por la app

| Método | Ruta | Uso |
|--------|------|-----|
| POST | `/api/auth/login` | Login |
| POST | `/api/auth/register` | Alta de usuario |
| GET | `/api/products` | Listado |
| POST | `/api/products` | Alta producto (multipart) |
| PUT | `/api/products/:id` | Edición |
| DELETE | `/api/products/:id` | Baja lógica |
| GET | `/api/franchises` | Spinners |
| GET | `/api/providers` | Spinners |
