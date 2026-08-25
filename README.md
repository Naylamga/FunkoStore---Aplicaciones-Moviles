# Cómo hacer funcionar FunkoStore (login + productos + Supabase)

App Android con **inicio → login** + API Node.js + BD en Supabase (schema `funko_house`).

```
App  →  http://10.0.2.2:3000/api/...  →  Node  →  Supabase
```

## 1. Supabase

1. Proyecto en [supabase.com](https://supabase.com) (si está en pausa, **Restore**)
2. Si aún no corriste el schema: SQL Editor → `backend/supabase/schema.sql`
3. Storage → bucket `productos` **público** (para imágenes)
4. **Settings → API**: Project URL + Secret key (`sb_secret_...` o `service_role`)

## 2. API

```bash
cd backend
copy .env.example .env
# completar SUPABASE_URL y SUPABASE_SERVICE_ROLE_KEY
npm install
npm run dev
```

Emulador usa: `http://10.0.2.2:3000/api/`

## 3. App Android

1. Abrí `FunkoStore-App` en Android Studio
2. Run en emulador
3. Flujo: **INICIO** → **INICIAR** → **Login** (o Crear cuenta)

## Cuentas de prueba (BD actual)

Las cuentas creadas en pruebas de la API (contraseña en claro):

| Email | Contraseña | Rol típico |
|--------|------------|------------|
| `test89446394@funko.local` | `123456` | Cliente |
| `admin_test668164810@funko.local` | `123456` | Cliente |

> El nombre “Admin Test” **no** implica rol Administrador. El registro por app siempre crea rol **Cliente**. Para gestionar productos alcanza con estar logueado.

## Endpoints

| Método | Ruta | Uso |
|--------|------|-----|
| POST | `/api/auth/login` | Login |
| POST | `/api/auth/register` | Crear cuenta |
| GET/POST/PUT/DELETE | `/api/products` | Inventario |
| GET | `/api/franchises` | Franquicias |
| GET | `/api/providers` | Proveedores |
