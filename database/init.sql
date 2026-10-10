CREATE DATABASE IF NOT EXISTS cotizaciones CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cotizaciones;

CREATE TABLE IF NOT EXISTS productos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  descripcion VARCHAR(500),
  precio DECIMAL(12,2) NOT NULL,
  activo BIT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS cotizaciones (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  numero VARCHAR(20) UNIQUE,
  cliente_nombre VARCHAR(150) NOT NULL,
  cliente_documento VARCHAR(20),
  cliente_email VARCHAR(150),
  cliente_telefono VARCHAR(30),
  fecha DATE NOT NULL,
  subtotal DECIMAL(12,2) NOT NULL,
  igv DECIMAL(12,2) NOT NULL,
  total DECIMAL(12,2) NOT NULL,
  estado VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS detalle_cotizacion (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  cotizacion_id BIGINT NOT NULL,
  producto_id BIGINT,
  concepto VARCHAR(255) NOT NULL,
  cantidad INT NOT NULL,
  precio_unitario DECIMAL(12,2) NOT NULL,
  subtotal DECIMAL(12,2) NOT NULL,
  FOREIGN KEY (cotizacion_id) REFERENCES cotizaciones(id) ON DELETE CASCADE
);

INSERT INTO productos (nombre, descripcion, precio, activo) VALUES
 ('Desarrollo de página web', 'Sitio web institucional de hasta 5 secciones', 1500.00, 1),
 ('Soporte técnico mensual', 'Mantenimiento y soporte remoto', 300.00, 1),
 ('Hosting + dominio anual', 'Hosting compartido y dominio .com', 250.00, 1);
