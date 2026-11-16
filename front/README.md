# Sumac Yanahuara · Front-end (React + Vite + Bootstrap)

## Cómo correrlo
1. Backend: levanta `sistema-ventas` (Spring Boot, puerto 8080) con MySQL y el script `sistema_ventas.sql`.
2. En `application.properties` agrega esta línea para que el front pueda mostrar los mensajes del backend
   ("Stock insuficiente…", etc.):
       server.error.include-message=always
3. En esta carpeta: `npm install` y luego `npm run dev` → http://localhost:5173
   (el proxy de `vite.config.js` envía `/api` al 8080, no hace falta configurar CORS).

Usuario inicial: admin@sumac.local (la contraseña es la que generó el hash del script SQL).

## Qué incluye
Login por rol (ADMIN / EMPLEADO) · Inicio con KPIs · Pedidos (crear con varias líneas, ver detalle, confirmar venta)
· Clientes · Productos (medidor de stock vs. mínimo) · Inventario (entradas/salidas) · Alertas de stock
· Reportes PDF (6) · Entregas · Proveedores, Abastecimiento y Usuarios (solo ADMIN).

## Cobertura
Nada: ya están todos los módulos del backend.
