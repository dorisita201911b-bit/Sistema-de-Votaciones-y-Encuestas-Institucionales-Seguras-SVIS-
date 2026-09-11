-- =====================================================================
--  SISTEMA DE VOTACIONES Y ENCUESTAS INSTITUCIONALES SEGURAS (SVIS)
--  Instructor / Diseñador Curricular: Osman Alonso Aranguren Escobar
--  Base de Datos MySQL - Motor InnoDB (Soporte Transaccional ACID y Bloqueo de Concurrencia)
--  Ejecutar:  mysql -u root -p < schema.sql
-- =====================================================================

CREATE DATABASE IF NOT EXISTS svis_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE svis_db;

-- ---------------------------------------------------------------------
-- Eliminación ordenada respetando llaves foráneas
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS voto_recibo;
DROP TABLE IF EXISTS token_otp;
DROP TABLE IF EXISTS opcion;
DROP TABLE IF EXISTS encuesta;
DROP TABLE IF EXISTS usuario;

-- ---------------------------------------------------------------------
-- 1. Tabla: usuario (Padrón electoral y administradores)
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    documento       VARCHAR(30)  NOT NULL UNIQUE,
    nombre_completo VARCHAR(150) NOT NULL,
    email           VARCHAR(150),
    password        VARCHAR(100) NOT NULL,
    rol             ENUM('ADMIN', 'VOTANTE') NOT NULL DEFAULT 'VOTANTE',
    fecha_registro  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_usuario_doc (documento)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. Tabla: encuesta (Consultas democráticas y elecciones)
-- ---------------------------------------------------------------------
CREATE TABLE encuesta (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo          VARCHAR(200) NOT NULL,
    descripcion     TEXT,
    estado          ENUM('ACTIVA', 'CERRADA') NOT NULL DEFAULT 'ACTIVA',
    fecha_creacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre    DATETIME NULL,
    INDEX idx_encuesta_estado (estado)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. Tabla: opcion (Candidatos u opciones de respuesta)
-- CRÍTICO - REGLA 4 (Secreto Absoluto del Sufragio):
-- Es estrictamente prohibido registrar id de usuario o id de token aquí.
-- El voto únicamente incrementa el contador numérico 'votos_conteo' (+1).
-- ---------------------------------------------------------------------
CREATE TABLE opcion (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    encuesta_id     BIGINT NOT NULL,
    titulo          VARCHAR(200) NOT NULL,
    descripcion     VARCHAR(500) NULL,
    votos_conteo    INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_opcion_encuesta
        FOREIGN KEY (encuesta_id) REFERENCES encuesta(id) ON DELETE CASCADE,
    INDEX idx_opcion_encuesta (encuesta_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. Tabla: token_otp (Credenciales de un solo uso por encuesta)
-- CRÍTICO - REGLA 1 (Unicidad en Base de Datos):
-- Un usuario solo puede tener un único token generado por cada encuesta.
-- Se asegura a nivel de motor mediante CONSTRAINT UNIQUE(encuesta_id, usuario_id).
-- ---------------------------------------------------------------------
CREATE TABLE token_otp (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    encuesta_id       BIGINT NOT NULL,
    usuario_id        BIGINT NOT NULL,
    token             VARCHAR(64) NOT NULL UNIQUE,
    estado            ENUM('DISPONIBLE', 'USADO') NOT NULL DEFAULT 'DISPONIBLE',
    fecha_generacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion  DATETIME NOT NULL,
    fecha_uso         DATETIME NULL,
    CONSTRAINT fk_token_encuesta
        FOREIGN KEY (encuesta_id) REFERENCES encuesta(id) ON DELETE CASCADE,
    CONSTRAINT fk_token_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT uq_encuesta_usuario
        UNIQUE (encuesta_id, usuario_id),
    INDEX idx_token_valor (token),
    INDEX idx_token_estado (estado)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. Tabla: voto_recibo (Auditoría anónima y comprobante digital)
-- CRÍTICO - REGLA 4: NO vincula usuario_id ni opcion_id con el token.
-- Solo almacena el hash SHA-256 para comprobar que el voto fue emitido.
-- ---------------------------------------------------------------------
CREATE TABLE voto_recibo (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    encuesta_id     BIGINT NOT NULL,
    hash_recibo     VARCHAR(64) NOT NULL UNIQUE,
    fecha_voto      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_recibo_encuesta
        FOREIGN KEY (encuesta_id) REFERENCES encuesta(id) ON DELETE CASCADE,
    INDEX idx_recibo_hash (hash_recibo)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- DATOS SEMILLA / POBLACIÓN INICIAL
-- ---------------------------------------------------------------------

-- Usuarios del sistema (1 Administrador y 4 Aprendices / Estudiantes)
INSERT INTO usuario (documento, nombre_completo, email, password, rol) VALUES
('ADMIN01',  'Osman Alonso Aranguren Escobar', 'instructor.aranguren@sena.edu.co', 'admin123', 'ADMIN'),
('1001',     'Carlos Andrés Mendoza Rivas',   'carlos.mendoza@soy.sena.edu.co',     'aprendiz123', 'VOTANTE'),
('1002',     'María Fernanda Gómez Castro',   'maria.gomez@soy.sena.edu.co',       'aprendiz123', 'VOTANTE'),
('1003',     'Juan David Rodríguez Mora',     'juan.rodriguez@soy.sena.edu.co',    'aprendiz123', 'VOTANTE'),
('1004',     'Laura Valentina Peña Silva',    'laura.pena@soy.sena.edu.co',        'aprendiz123', 'VOTANTE');

-- Encuesta inicial demostrativa
INSERT INTO encuesta (id, titulo, descripcion, estado, fecha_creacion) VALUES
(1,
 'Elección de Representante de Aprendices ADSO 2026',
 'Jornada democrática institucional para elegir el vocero principal de la ficha ADSO ante el comité académico.',
 'ACTIVA',
 NOW());

-- Opciones electorales para la Encuesta 1 (Contador de votos inicia en 0)
INSERT INTO opcion (encuesta_id, titulo, descripcion, votos_conteo) VALUES
(1, 'Candidato 1: Daniel Ospina - Lista A', 'Propuestas: Fortalecimiento de laboratorios y semilleros de investigación.', 0),
(1, 'Candidato 2: Sofía Herrera - Lista B', 'Propuestas: Tutorías entre pares y convenios de prácticas empresariales.', 0),
(1, 'Voto en Blanco',                      'Opción institucional de abstención de preferencia electoral.', 0);

-- Tokens OTP de prueba para los aprendices (Válidos por 24 horas)
-- Un usuario solo tiene un token para la encuesta 1 (REGLA 1)
INSERT INTO token_otp (encuesta_id, usuario_id, token, estado, fecha_generacion, fecha_expiracion) VALUES
(1, 2, 'OTP-ADSO-1001-A9F2', 'DISPONIBLE', NOW(), DATE_ADD(NOW(), INTERVAL 24 HOUR)),
(1, 3, 'OTP-ADSO-1002-B7E4', 'DISPONIBLE', NOW(), DATE_ADD(NOW(), INTERVAL 24 HOUR)),
(1, 4, 'OTP-ADSO-1003-C3D8', 'DISPONIBLE', NOW(), DATE_ADD(NOW(), INTERVAL 24 HOUR)),
(1, 5, 'OTP-ADSO-1004-D1A6', 'DISPONIBLE', NOW(), DATE_ADD(NOW(), INTERVAL 24 HOUR));
