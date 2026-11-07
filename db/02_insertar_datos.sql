-- Mi base de datos de música
-- Kevin Quiroz, Joselyn Nuñez y Mariana González 

USE MiBDM;

-- usuario
INSERT INTO usuario (id_usuario, nombre, apellido, correo, pais, fecha_registro) VALUES
(1, 'Mariana','González', 'mari.gonzalez@gmail.com', 'Costa Rica', '2025-11-10'),
(2, 'Joselyn','Nuñez','juno.numi@gmail.com','Costa Rica', '2025-12-02'),
(3, 'Kevin','Quiroz','kevin.quiroz@gmail.com', 'Costa Rica','2026-01-15');

-- usuario tipo artista
INSERT INTO usuario (id_usuario, nombre, apellido, correo, pais, fecha_registro, tipo) VALUES
(4, 'Mateo','Solís','mateo.solis@example.com', 'Costa Rica','2026-02-01', 'artista');

-- artista
INSERT INTO artista (id_artista, nombre, pais_origen, tipo) VALUES
(1, 'Soda Stereo','Argentina','banda'),
(2, 'Gustavo Cerati','Argentina','solista'),
(3, 'Daft Punk','Francia','banda'),
(4, 'Pharrell Williams','Estados Unidos','solista'),
(5, 'Bad Bunny','Puerto Rico', 'solista'),
(6, 'Billie Eilish','Estados Unidos','solista'),
(7, 'FINNEAS','Estados Unidos','solista');

-- album
INSERT INTO album (id_album, titulo, anio_lanzamiento, tipo, id_artista) VALUES
(1, 'Canción Animal', 1990, 'LP', 1),
(2, 'Random Access Memories',  2013, 'LP', 3),
(3, 'Un Verano Sin Ti', 2022, 'LP', 5),
(4, 'When We All Fall Asleep, Where Do We Go?',2019, 'LP', 6);

-- cancion  (la canción 8 es un sencillo: sin álbum ni número de pista)
INSERT INTO cancion (id_cancion, titulo, duracion_seg, numero_pista, id_album) VALUES
(1, 'De música ligera',212, 1,1),
(2, 'Get Lucky', 369, 8, 2),
(3, 'Instant Crush', 337, 5,2),
(4, 'Lose Yourself to Dance', 353, 6,2),
(5, 'Me Porto Bonito', 178, 3, 3),
(6, 'Tití Me Preguntó',243, 4, 3),
(7, 'bad guy', 194, 2,    4),
(8, 'No Time to Die',242, NULL, NULL);

-- genero
INSERT INTO genero (id_genero, nombre) VALUES
(1, 'Rock en español'),
(2, 'Electrónica'),
(3, 'Reguetón'),
(4, 'Pop alternativo'),
(5, 'Disco');

-- plataforma
INSERT INTO plataforma (id_plataforma, nombre, tipo) VALUES
(1, 'Spotify','streaming'),
(2, 'YouTube Music', 'streaming'),
(3, 'Vinilo','fisico'),
(4, 'Radio','radio');

-- cancion_artista  (participa + rol)
INSERT INTO cancion_artista (id_cancion, id_artista, rol) VALUES
(1, 1, 'interprete'),
(1, 2, 'compositor'),
(2, 3, 'interprete'),
(2, 4, 'interprete'),
(3, 3, 'interprete'),
(4, 3, 'interprete'),
(5, 5, 'interprete'),
(6, 5, 'interprete'),
(7, 6, 'interprete'),
(7, 7, 'compositor'),
(8, 6, 'interprete'),
(8, 7, 'compositor');

-- cancion_genero  (clasifica)
INSERT INTO cancion_genero (id_cancion, id_genero) VALUES
(1, 1),
(2, 2), (2, 5),
(3, 2),
(4, 2), (4, 5),
(5, 3),
(6, 3),
(7, 4),
(8, 4);

-- escucha  (historial por usuario)
INSERT INTO escucha (id_escucha, id_usuario, id_cancion, id_plataforma, fecha_hora, segundos_escuchados) VALUES
-- Ana (1)
( 1, 1, 2, 1, '2026-01-05 08:15:00', 369),
( 2, 1, 3, 1, '2026-01-05 08:21:10', 337),
( 3, 1, 4, 1, '2026-01-05 08:27:05', 353),
( 4, 1, 2, 1, '2026-01-12 18:40:00', 369),
( 5, 1, 2, 2, '2026-02-03 07:50:30', 369),
( 6, 1, 1, 3, '2026-02-14 20:10:00', 212),
( 7, 1, 7, 1, '2026-03-01 12:00:00', 194),
( 8, 1, 8, 2, '2026-03-02 22:30:00', 242),
( 9, 1, 5, 1, '2026-04-10 17:05:00', 178),
(10, 1, 6, 1, '2026-04-10 17:08:00', 243),
-- Luis (2)
(11, 2, 5, 1, '2026-01-20 09:00:00', 178),
(12, 2, 6, 1, '2026-01-20 09:03:00', 243),
(13, 2, 5, 1, '2026-01-27 09:00:00', 178),
(14, 2, 1, 4, '2026-02-08 06:45:00', 212),
(15, 2, 7, 2, '2026-02-15 19:20:00', 194),
(16, 2, 7, 2, '2026-02-16 19:25:00', 194),
(17, 2, 2, 1, '2026-03-05 21:00:00', 200),
(18, 2, 3, 1, '2026-03-05 21:07:00', 337),
-- Camila (3)
(19, 3, 1, 1, '2026-01-30 15:00:00', 212),
(20, 3, 8, 2, '2026-02-22 23:10:00', 242),
(21, 3, 7, 1, '2026-03-18 10:30:00', 194),
(22, 3, 5, 2, '2026-04-02 13:15:00', 178);

-- escucha de album completo (segundos = suma de las canciones registradas del album)
INSERT INTO escucha (id_escucha, id_usuario, id_album, id_plataforma, fecha_hora, segundos_escuchados) VALUES
(23, 1, 2, 1, '2026-04-18 19:00:00', 1059),
(24, 2, 3, 1, '2026-02-20 20:30:00', 421),
(25, 3, 1, 3, '2026-03-25 21:00:00', 212),
(26, 2, 4, 2, '2026-04-05 18:00:00', 194);

-- calificacion
INSERT INTO calificacion (id_usuario, id_cancion, puntuacion, fecha, comentario) VALUES
(1, 2, 5, '2026-01-06', 'Imprescindible para entrenar'),
(1, 7, 4, '2026-03-01', 'Muy buen bajo'),
(1, 5, 4, '2026-04-10', NULL),
(2, 5, 5, '2026-01-21', 'Mi canción del verano'),
(2, 7, 3, '2026-02-16', 'Buena, pero ya la escuché mucho'),
(2, 1, 5, '2026-02-08', 'Clásico del rock en español'),
(3, 1, 4, '2026-01-30', NULL),
(3, 8, 4, '2026-02-22', 'Muy cinematográfica'),
(3, 7, 5, '2026-03-18', 'Favorita de la semana');

-- calificacion_album
INSERT INTO calificacion_album (id_usuario, id_album, puntuacion, fecha, comentario) VALUES
(1, 2, 5, '2026-04-18', 'Disco redondo, sin relleno'),
(2, 3, 4, '2026-02-20', NULL),
(3, 1, 5, '2026-03-25', 'Clásico que suena increíble en vinilo'),
(4, 2, 5, '2026-02-10', 'Gran producción');

-- por_escuchar  (música pendiente por descubrir)
INSERT INTO por_escuchar (id_usuario, id_album, fecha_agregado, prioridad, estado) VALUES
(1, 4, '2026-03-01', 'alta',  'pendiente'),
(1, 1, '2026-02-14', 'media', 'pendiente'),
(2, 2, '2026-03-05', 'alta',  'pendiente'),
(2, 3, '2026-01-19', 'baja',  'escuchado'),
(3, 2, '2026-02-01', 'media', 'pendiente'),
(3, 3, '2026-04-02', 'alta',  'pendiente');
