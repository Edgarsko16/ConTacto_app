CREATE DATABASE IF NOT EXISTS contacto_app;
USE contacto_app;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS mensajes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contenido VARCHAR(255) NOT NULL,
    fecha DATETIME NOT NULL,
    usuario VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS necesidades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    icono VARCHAR(8) NOT NULL,
    patron_vibracion VARCHAR(100),
    mensaje_voz VARCHAR(150) NOT NULL,
    css_class VARCHAR(50) NOT NULL,
    activa BOOLEAN NOT NULL
);

INSERT INTO usuarios (username, password, rol)
SELECT 'admin', '1234', 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE username = 'admin'
);

INSERT INTO usuarios (username, password, rol)
SELECT 'usuario', '1234', 'USUARIO'
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE username = 'usuario'
);

INSERT INTO usuarios (username, password, rol)
SELECT 'profesional', '1234', 'PROFESIONAL'
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE username = 'profesional'
);

INSERT INTO necesidades (nombre, icono, patron_vibracion, mensaje_voz, css_class, activa)
SELECT 'Agua', '💧', '200', 'Necesito agua', 'need-water', true
WHERE NOT EXISTS (SELECT 1 FROM necesidades WHERE nombre = 'Agua');
