-- =====================================================
-- BASE DE DATOS: SVIS
-- Sistema de Votaciones y Encuestas Institucionales Seguras
-- =====================================================

CREATE DATABASE IF NOT EXISTS sistemavotaciones
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE sistemavotaciones;

-- Eliminación ordenada respetando llaves foráneas
DROP TABLE IF EXISTS votos;
DROP TABLE IF EXISTS tokens;
DROP TABLE IF EXISTS opciones;
DROP TABLE IF EXISTS encuestas;
DROP TABLE IF EXISTS usuarios;

-- =====================================================
-- TABLA 1: USUARIOS
-- =====================================================
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ADMIN', 'VOTANTE') NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- =====================================================
-- TABLA 2: ENCUESTAS
-- =====================================================
CREATE TABLE encuestas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    estado ENUM('ACTIVA', 'CERRADA') NOT NULL DEFAULT 'CERRADA',
    fecha_inicio DATETIME NULL,
    fecha_fin DATETIME NULL,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =====================================================
-- TABLA 3: OPCIONES
-- =====================================================
CREATE TABLE opciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    encuesta_id INT NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    cantidad_votos INT NOT NULL DEFAULT 0,

    CONSTRAINT fk_opciones_encuesta
        FOREIGN KEY (encuesta_id)
        REFERENCES encuestas(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =====================================================
-- TABLA 4: TOKENS OTP
-- =====================================================
CREATE TABLE tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    encuesta_id INT NOT NULL,
    usuario_id INT NOT NULL,
    token VARCHAR(255) NOT NULL,
    estado ENUM('DISPONIBLE', 'USADO') NOT NULL DEFAULT 'DISPONIBLE',
    fecha_expiracion DATETIME NOT NULL,
    fecha_uso DATETIME NULL,

    -- Un usuario solamente puede tener un token por encuesta (REGLA 1)
    CONSTRAINT uk_encuesta_usuario
        UNIQUE (encuesta_id, usuario_id),

    -- El token también debe ser único
    CONSTRAINT uk_token
        UNIQUE (token),

    CONSTRAINT fk_tokens_encuesta
        FOREIGN KEY (encuesta_id)
        REFERENCES encuestas(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_tokens_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =====================================================
-- TABLA 5: VOTOS
-- =====================================================
CREATE TABLE votos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    encuesta_id INT NOT NULL,
    opcion_id INT NOT NULL,
    fecha_voto DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    comprobante_hash VARCHAR(255) NOT NULL UNIQUE,

    CONSTRAINT fk_votos_encuesta
        FOREIGN KEY (encuesta_id)
        REFERENCES encuestas(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_votos_opcion
        FOREIGN KEY (opcion_id)
        REFERENCES opciones(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =====================================================
-- DATOS SEMILLA INSTITUCIONALES (SENA CIMM)
-- =====================================================

INSERT INTO usuarios (id, nombre, correo, password, rol, activo) VALUES
(1, 'Andrea Martínez Cruz', 'andrea.martinez@sena.edu.co', 'admin123', 'ADMIN', TRUE),
(2, 'Carlos Andrés Mendoza Rivas', 'carlos.mendoza@soy.sena.edu.co', 'aprendiz123', 'VOTANTE', TRUE),
(3, 'María Fernanda Gómez Castro', 'maria.gomez@soy.sena.edu.co', 'aprendiz123', 'VOTANTE', TRUE),
(4, 'Juan David Rodríguez Mora', 'juan.rodriguez@soy.sena.edu.co', 'aprendiz123', 'VOTANTE', TRUE),
(5, 'Laura Valentina Peña Silva', 'laura.pena@soy.sena.edu.co', 'aprendiz123', 'VOTANTE', TRUE),
(6, 'Doris Yasmín López Chocontá', 'doris.lopez@sena.edu.co', 'admin123', 'ADMIN', TRUE);

INSERT INTO encuestas (id, titulo, descripcion, estado, fecha_inicio, fecha_fin, fecha_creacion) VALUES
(1, 'Elección de Representante de Aprendices ADSO 2026',
 'Jornada democrática institucional para elegir el vocero principal de la ficha ADSO ante el comité académico.',
 'ACTIVA', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), NOW());

INSERT INTO opciones (id, encuesta_id, nombre, cantidad_votos) VALUES
(1, 1, 'Candidato 1: Daniel Ospina - Lista A (Semilleros e Innovación)', 0),
(2, 1, 'Candidato 2: Sofía Herrera - Lista B (Tutorías y Bienestar)', 0),
(3, 1, 'Voto en Blanco Institucional', 0);

-- Tokens iniciales para la encuesta 1
INSERT INTO tokens (encuesta_id, usuario_id, token, estado, fecha_expiracion) VALUES
(1, 2, 'OTP-ADSO-1001-A9F2', 'DISPONIBLE', DATE_ADD(NOW(), INTERVAL 24 HOUR)),
(1, 3, 'OTP-ADSO-1002-B7E4', 'DISPONIBLE', DATE_ADD(NOW(), INTERVAL 24 HOUR)),
(1, 4, 'OTP-ADSO-1003-C3D8', 'DISPONIBLE', DATE_ADD(NOW(), INTERVAL 24 HOUR)),
(1, 5, 'OTP-ADSO-1004-D1A6', 'DISPONIBLE', DATE_ADD(NOW(), INTERVAL 24 HOUR));
