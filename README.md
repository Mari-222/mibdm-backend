# MiBDM – BackEnd (Entrega #2, Bases de Datos I)

API REST en **Spring Boot 3 + Spring JDBC (`JdbcTemplate`)** sobre la base de datos de música **MiBDM** (MySQL 8).
Código organizado con el patrón **Repositorio**:

```
controller  → recibe HTTP y devuelve JSON
service     → reglas de negocio y validaciones
repository  → SQL con JdbcTemplate (interfaz + implementación)
model       → records usados como entrada y salida (DTO)
exception   → errores con código HTTP y manejador global
```

Grupo 2: Mariana González, Kevin Quiroz y Joselyn Nuñez.

## Cómo ejecutarlo

Requisitos: Java 17+, Maven (o IntelliJ), Docker Desktop.

```bash
# 1. Levantar MySQL con las tablas y los datos (puerto 3307)
docker compose up -d

# 2. Ejecutar la API (http://localhost:8080)
mvn spring-boot:run
```

* Scripts: `db/01_creacion_base.sql` (tablas, restricciones y vista) y `db/02_insertar_datos.sql` (datos de prueba).
  Docker los ejecuta solo la primera vez. Para reiniciar la base desde cero: `docker compose down -v && docker compose up -d`.
* Conexión por defecto (`application.properties`): `localhost:3307`, usuario `root`, contraseña `root1234`.
  Se puede cambiar con las variables de entorno `DB_URL`, `DB_USER` y `DB_PASSWORD`.

## Convenciones

* Base: `http://localhost:8080/api`. Todo se envía y recibe en JSON.
* Los errores tienen la forma `{ "error": "mensaje" }` con el código HTTP correspondiente:
  `400` datos inválidos, `403` operación no permitida, `404` no existe, `409` conflicto (duplicado o restricción).
* No hay contraseñas: el usuario se identifica por su correo (`/auth/login`) y el FrontEnd guarda su `idUsuario`.
  Los endpoints de un usuario llevan su id en la ruta (`/usuarios/{idUsuario}/...`).

## Endpoints

### Usuarios (`usuario`)

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/auth/login` | Cuerpo `{"correo": "..."}`. Devuelve el usuario o 404. |
| POST | `/usuarios` | Registra un usuario normal. Cuerpo `{"nombre","apellido","correo","pais"}`. 201, o 409 si el correo ya existe. |
| GET | `/usuarios/{idUsuario}` | Datos del usuario. |

### Catálogo (`artista`, `album`, `cancion`, `genero`, `plataforma`, `cancion_artista`, `cancion_genero`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/artistas?q=` | Lista de artistas; `q` filtra por nombre. |
| GET | `/artistas/{idArtista}` | Artista y sus álbumes. |
| GET | `/albumes?q=` | Lista de álbumes (incluye `numCanciones`); `q` filtra por título. |
| GET | `/albumes/{idAlbum}` | Álbum, sus canciones ordenadas por pista, promedio y total de calificaciones. |
| GET | `/canciones?q=&idGenero=` | Lista de canciones con intérpretes y géneros; filtros opcionales. |
| GET | `/canciones/{idCancion}` | Canción, participantes con su rol, géneros y promedio de calificaciones. |
| GET | `/generos` | Lista de géneros. |
| GET | `/plataformas` | Lista de plataformas. |
| POST | `/albumes` | **Solo usuarios tipo `artista`** (403 si no). Cuerpo `{"idUsuario","titulo","anioLanzamiento","tipo","idArtista"}`; `tipo` es `LP`, `EP`, `sencillo` o `recopilatorio` (por defecto `LP`). |
| POST | `/canciones` | **Solo usuarios tipo `artista`**. Cuerpo `{"idUsuario","titulo","duracionSeg","numeroPista","idAlbum","idsInterpretes":[..],"idsGeneros":[..]}`. `numeroPista` e `idAlbum` son opcionales (sencillo). Se inserta en `cancion`, `cancion_artista` y `cancion_genero` en una transacción. 409 si la pista ya existe en el álbum. |

### Escuchas (`escucha`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/usuarios/{idUsuario}/escuchas?limite=50` | Historial, de la más reciente a la más antigua. `tipoItem` es `cancion` o `album`. |
| POST | `/usuarios/{idUsuario}/escuchas` | Registra una escucha. Cuerpo `{"idCancion" **o** "idAlbum", "idPlataforma", "segundosEscuchados", "fechaHora"}`. Se debe enviar `idCancion` o `idAlbum`, nunca ambos. Si faltan `segundosEscuchados` se usa la duración completa y si falta `fechaHora` la fecha actual (`2026-04-18T19:00:00`). |
| DELETE | `/usuarios/{idUsuario}/escuchas/{idEscucha}` | Elimina una escucha del usuario (204). |

### Calificaciones (`calificacion`, `calificacion_album`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/usuarios/{idUsuario}/calificaciones/canciones` | Calificaciones de canciones del usuario. |
| PUT | `/usuarios/{idUsuario}/calificaciones/canciones/{idCancion}` | Califica o modifica. Cuerpo `{"puntuacion": 1-5, "comentario": "opcional"}`. 204. |
| DELETE | `/usuarios/{idUsuario}/calificaciones/canciones/{idCancion}` | Elimina la calificación (204). |
| GET | `/usuarios/{idUsuario}/calificaciones/albumes` | Calificaciones de álbumes del usuario. |
| PUT | `/usuarios/{idUsuario}/calificaciones/albumes/{idAlbum}` | Califica o modifica un álbum (mismo cuerpo). 204. |
| DELETE | `/usuarios/{idUsuario}/calificaciones/albumes/{idAlbum}` | Elimina la calificación (204). |

### Por escuchar (`por_escuchar`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/usuarios/{idUsuario}/por-escuchar` | Lista del usuario (pendientes primero, luego por prioridad). |
| POST | `/usuarios/{idUsuario}/por-escuchar` | Agrega un álbum. Cuerpo `{"idAlbum", "prioridad"}`; `prioridad` es `alta`, `media` (por defecto) o `baja`. 201, o 409 si ya está en la lista. |
| PUT | `/usuarios/{idUsuario}/por-escuchar/{idAlbum}` | Cambia `prioridad` y/o `estado` (`pendiente`, `escuchado`, `descartado`). 204. |
| DELETE | `/usuarios/{idUsuario}/por-escuchar/{idAlbum}` | Quita el álbum de la lista (204). |

### Estadísticas

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/usuarios/{idUsuario}/estadisticas` | Total de escuchas y de segundos, top 5 de canciones, artistas y géneros, y escuchas por plataforma. |

### Ejemplos

```bash
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" \
     -d '{"correo":"mari.gonzalez@gmail.com"}'

curl -X POST http://localhost:8080/api/usuarios/1/escuchas -H "Content-Type: application/json" \
     -d '{"idCancion":2,"idPlataforma":1}'

curl -X PUT http://localhost:8080/api/usuarios/1/calificaciones/canciones/2 -H "Content-Type: application/json" \
     -d '{"puntuacion":5,"comentario":"Imprescindible"}'
```

## Tablas usadas por cada funcionalidad

| Tabla | Operaciones |
|---|---|
| `usuario` | leer, insertar (registro) |
| `artista` | leer |
| `album` | leer, insertar (usuario artista) |
| `cancion` | leer, insertar (usuario artista) |
| `genero` | leer |
| `plataforma` | leer |
| `cancion_artista` | leer, insertar (usuario artista) |
| `cancion_genero` | leer, insertar (usuario artista) |
| `escucha` | leer, insertar, borrar |
| `calificacion` | leer, insertar/actualizar, borrar |
| `calificacion_album` | leer, insertar/actualizar, borrar |
| `por_escuchar` | leer, insertar, actualizar, borrar |

La vista `vw_resumen_escuchas` está creada en la base de datos, pero la API consulta las tablas directamente
(la vista repite filas cuando una canción tiene varios intérpretes).
