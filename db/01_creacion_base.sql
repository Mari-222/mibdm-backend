-- Mi Base de Datos de Música
-- Grupo 2: Mariana Gonzalez, Kevin Quiroz , Joselyn Nuñez 

-- Creacion 
DROP DATABASE IF EXISTS MiBDM;
CREATE DATABASE MiBDM
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;
USE MiBDM;

-- Entidades de la BD
CREATE TABLE artista (
    id_artista INT AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    pais_origen  VARCHAR(50),
    tipo ENUM('solista','banda') NOT NULL,
    CONSTRAINT pk_artista PRIMARY KEY (id_artista)
);
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT,
    nombre VARCHAR(50)  NOT NULL,
    apellido VARCHAR(50)  NOT NULL,
    correo VARCHAR(100) NOT NULL,
    pais VARCHAR(50),
    fecha_registro  DATE NOT NULL DEFAULT (CURRENT_DATE),
    tipo ENUM('normal','artista') NOT NULL DEFAULT 'normal', -- normal o artista (el artista registra su catalogo)
    id_artista INT NULL, -- perfil de artista del usuario tipo 'artista' (NULL para usuarios normales)
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_correo UNIQUE (correo),
    CONSTRAINT uq_usuario_artista UNIQUE (id_artista),
    CONSTRAINT fk_usuario_artista FOREIGN KEY (id_artista)
        REFERENCES artista (id_artista)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT ck_usuario_artista CHECK ((tipo = 'artista') = (id_artista IS NOT NULL))
);
CREATE TABLE album (
    id_album INT AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    anio_lanzamiento SMALLINT NOT NULL,
    tipo ENUM('LP','EP','sencillo','recopilatorio') NOT NULL DEFAULT 'LP',
    id_artista INT  NOT NULL,  -- relacion total
    CONSTRAINT pk_album PRIMARY KEY (id_album),
    CONSTRAINT fk_album_artista FOREIGN KEY (id_artista)
        REFERENCES artista (id_artista)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_album_anio CHECK (anio_lanzamiento BETWEEN 1900 AND 2100)
);
CREATE TABLE cancion (
    id_cancion INT AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    duracion_seg  INT NOT NULL,
    numero_pista  SMALLINT NULL, -- hay canciones que no pertenecen a album
    id_album INT NULL,  -- relac con album
    CONSTRAINT pk_cancion PRIMARY KEY (id_cancion),
    CONSTRAINT fk_cancion_album FOREIGN KEY (id_album)
        REFERENCES album (id_album)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT uq_cancion_pista UNIQUE (id_album, numero_pista),
    CONSTRAINT ck_cancion_duracion CHECK (duracion_seg > 0),
    CONSTRAINT ck_cancion_pista CHECK (numero_pista IS NULL OR numero_pista > 0)
);
CREATE TABLE genero (
    id_genero INT AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    CONSTRAINT pk_genero PRIMARY KEY (id_genero),
    CONSTRAINT uq_genero_nombre UNIQUE (nombre)
);
CREATE TABLE plataforma (
    id_plataforma  INT AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    tipo ENUM('streaming','fisico','radio','concierto','otro') NOT NULL,
    CONSTRAINT pk_plataforma PRIMARY KEY (id_plataforma),
    CONSTRAINT uq_plataforma_nombre UNIQUE (nombre)
);
CREATE TABLE escucha (
    id_escucha INT AUTO_INCREMENT,
    fecha_hora DATETIME NOT NULL,
    segundos_escuchados INT NOT NULL,
    id_usuario INT NOT NULL, -- relaciones
    id_cancion INT NULL,  -- escucha de cancion o de album (solo uno de los dos)
    id_album INT NULL,
    id_plataforma INT NOT NULL,             
    CONSTRAINT pk_escucha PRIMARY KEY (id_escucha),
    CONSTRAINT fk_escucha_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_escucha_cancion FOREIGN KEY (id_cancion)
        REFERENCES cancion (id_cancion)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_escucha_album FOREIGN KEY (id_album)
        REFERENCES album (id_album)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_escucha_plataforma FOREIGN KEY (id_plataforma)
        REFERENCES plataforma (id_plataforma)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_escucha_segundos CHECK (segundos_escuchados >= 0),
    CONSTRAINT ck_escucha_cancion_o_album CHECK ((id_cancion IS NOT NULL) + (id_album IS NOT NULL) = 1)
);
CREATE INDEX idx_escucha_usuario_fecha ON escucha (id_usuario, fecha_hora);

-- tablas de relaciones
-- cancion–artista
CREATE TABLE cancion_artista (
    id_cancion INT NOT NULL,
    id_artista INT NOT NULL,
    rol ENUM('interprete','compositor','productor') NOT NULL,
    CONSTRAINT pk_cancion_artista PRIMARY KEY (id_cancion, id_artista, rol),
    CONSTRAINT fk_ca_cancion FOREIGN KEY (id_cancion)
        REFERENCES cancion (id_cancion)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_ca_artista FOREIGN KEY (id_artista)
        REFERENCES artista (id_artista)
        ON UPDATE CASCADE ON DELETE RESTRICT
);
-- cancion–genero
CREATE TABLE cancion_genero (
    id_cancion INT NOT NULL,
    id_genero INT NOT NULL,
    CONSTRAINT pk_cancion_genero PRIMARY KEY (id_cancion, id_genero),
    CONSTRAINT fk_cg_cancion FOREIGN KEY (id_cancion)
        REFERENCES cancion (id_cancion)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_cg_genero FOREIGN KEY (id_genero)
        REFERENCES genero (id_genero)
        ON UPDATE CASCADE ON DELETE RESTRICT
);
-- usuario–cancion
CREATE TABLE calificacion (
    id_usuario INT NOT NULL,
    id_cancion INT NOT NULL,
    puntuacion TINYINT NOT NULL,
    fecha DATE NOT NULL,
    comentario VARCHAR(255),
    CONSTRAINT pk_calificacion PRIMARY KEY(id_usuario, id_cancion),
    CONSTRAINT fk_cal_usuario FOREIGN KEY(id_usuario)
        REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_cal_cancion FOREIGN KEY (id_cancion)
        REFERENCES cancion (id_cancion)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT ck_cal_puntuacion CHECK (puntuacion BETWEEN 1 AND 5)
);
-- usuario–album (califica el album)
CREATE TABLE calificacion_album (
    id_usuario INT NOT NULL,
    id_album INT NOT NULL,
    puntuacion TINYINT NOT NULL,
    fecha DATE NOT NULL,
    comentario VARCHAR(255),
    CONSTRAINT pk_calificacion_album PRIMARY KEY(id_usuario, id_album),
    CONSTRAINT fk_cala_usuario FOREIGN KEY(id_usuario)
        REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_cala_album FOREIGN KEY (id_album)
        REFERENCES album (id_album)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT ck_cala_puntuacion CHECK (puntuacion BETWEEN 1 AND 5)
);
-- usuario–album
CREATE TABLE por_escuchar (
    id_usuario INT NOT NULL,
    id_album INT NOT NULL,
    fecha_agregado DATE NOT NULL,
    prioridad ENUM('alta','media','baja') NOT NULL DEFAULT 'media',
    estado ENUM('pendiente','escuchado','descartado') NOT NULL DEFAULT 'pendiente',
    CONSTRAINT pk_por_escuchar PRIMARY KEY (id_usuario, id_album),
    CONSTRAINT fk_pe_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_pe_album FOREIGN KEY (id_album)
        REFERENCES album (id_album)
        ON UPDATE CASCADE ON DELETE CASCADE
);
-- vista  
CREATE OR REPLACE VIEW vw_resumen_escuchas AS
SELECT
    e.id_escucha,
    u.id_usuario,
    CONCAT(u.nombre, ' ', u.apellido) AS usuario,
    e.fecha_hora,
    e.segundos_escuchados,
    c.id_cancion,
    c.titulo AS cancion,
    a.id_artista,
    a.nombre AS artista,
    al.titulo AS album,
    p.nombre AS plataforma,
    p.tipo AS tipo_plataforma
FROM escucha e
JOIN usuario u ON u.id_usuario = e.id_usuario
JOIN cancion c ON c.id_cancion = e.id_cancion
JOIN cancion_artista ca ON ca.id_cancion = c.id_cancion AND ca.rol = 'interprete'
JOIN artista a ON a.id_artista = ca.id_artista
LEFT JOIN album al ON al.id_album = c.id_album
JOIN plataforma p ON p.id_plataforma = e.id_plataforma
-- escuchas de album completo (id_cancion y cancion salen NULL)
UNION ALL
SELECT
    e.id_escucha,
    u.id_usuario,
    CONCAT(u.nombre, ' ', u.apellido) AS usuario,
    e.fecha_hora,
    e.segundos_escuchados,
    NULL AS id_cancion,
    NULL AS cancion,
    a.id_artista,
    a.nombre AS artista,
    al.titulo AS album,
    p.nombre AS plataforma,
    p.tipo AS tipo_plataforma
FROM escucha e
JOIN usuario u ON u.id_usuario = e.id_usuario
JOIN album al ON al.id_album = e.id_album
JOIN artista a ON a.id_artista = al.id_artista
JOIN plataforma p ON p.id_plataforma = e.id_plataforma;
