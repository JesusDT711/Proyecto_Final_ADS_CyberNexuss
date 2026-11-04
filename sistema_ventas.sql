
CREATE DATABASE sistema_ventas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sistema_ventas;

SHOW TABLES;
 
-- ======= TABLAS =======
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_roles_nombre (nombre)
);

-- ======= INSERTS INTO =======
INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN'),('ROLE_EMPLEADO');

INSERT INTO usuarios (nombre,correo,contrasena,activo,fecha_creacion,rol_id)
SELECT
    'Administrador Inicial',
    'admin@sumac.local',
    '$2a$10$Dhk8LvIg.Fpc9156R7YvHuPeb6KrUSMKjToMPAA.eVqnQI9PvKnPy',
    TRUE,
    CURRENT_TIMESTAMP,
    r.id
FROM roles r
WHERE r.nombre = 'ROLE_ADMIN'
AND NOT EXISTS (
    SELECT 1
    FROM usuarios
    WHERE correo = 'admin@sumac.local'
);


-- ======= SELECTS =======
SELECT id, nombre FROM roles;
SELECT * FROM usuarios;
SELECT * FROM productos;
SELECT * FROM clientes;
SELECT * FROM pedidos;
SELECT id, nombre, correo, activo, rol_id FROM usuarios;