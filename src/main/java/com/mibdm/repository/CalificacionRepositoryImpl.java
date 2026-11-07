package com.mibdm.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.mibdm.model.CalificacionVista;

@Repository
public class CalificacionRepositoryImpl implements CalificacionRepository {

    private static final RowMapper<CalificacionVista> MAPPER = (rs, i) -> new CalificacionVista(
            rs.getInt("id_item"),
            rs.getString("titulo"),
            rs.getInt("puntuacion"),
            rs.getDate("fecha").toLocalDate(),
            rs.getString("comentario"));

    private final JdbcTemplate jdbc;

    public CalificacionRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<CalificacionVista> listarCanciones(int idUsuario) {
        return jdbc.query("""
                SELECT k.id_cancion AS id_item, c.titulo, k.puntuacion, k.fecha, k.comentario
                FROM calificacion k
                JOIN cancion c ON c.id_cancion = k.id_cancion
                WHERE k.id_usuario = ?
                ORDER BY k.fecha DESC, c.titulo
                """, MAPPER, idUsuario);
    }

    @Override
    public void guardarCancion(int idUsuario, int idCancion, int puntuacion, String comentario) {
        jdbc.update("""
                INSERT INTO calificacion (id_usuario, id_cancion, puntuacion, fecha, comentario)
                VALUES (?, ?, ?, CURRENT_DATE, ?)
                ON DUPLICATE KEY UPDATE puntuacion = VALUES(puntuacion),
                                        fecha = CURRENT_DATE,
                                        comentario = VALUES(comentario)
                """, idUsuario, idCancion, puntuacion, comentario);
    }

    @Override
    public int eliminarCancion(int idUsuario, int idCancion) {
        return jdbc.update("DELETE FROM calificacion WHERE id_usuario = ? AND id_cancion = ?", idUsuario, idCancion);
    }

    @Override
    public List<CalificacionVista> listarAlbumes(int idUsuario) {
        return jdbc.query("""
                SELECT k.id_album AS id_item, al.titulo, k.puntuacion, k.fecha, k.comentario
                FROM calificacion_album k
                JOIN album al ON al.id_album = k.id_album
                WHERE k.id_usuario = ?
                ORDER BY k.fecha DESC, al.titulo
                """, MAPPER, idUsuario);
    }

    @Override
    public void guardarAlbum(int idUsuario, int idAlbum, int puntuacion, String comentario) {
        jdbc.update("""
                INSERT INTO calificacion_album (id_usuario, id_album, puntuacion, fecha, comentario)
                VALUES (?, ?, ?, CURRENT_DATE, ?)
                ON DUPLICATE KEY UPDATE puntuacion = VALUES(puntuacion),
                                        fecha = CURRENT_DATE,
                                        comentario = VALUES(comentario)
                """, idUsuario, idAlbum, puntuacion, comentario);
    }

    @Override
    public int eliminarAlbum(int idUsuario, int idAlbum) {
        return jdbc.update("DELETE FROM calificacion_album WHERE id_usuario = ? AND id_album = ?",
                idUsuario, idAlbum);
    }
}
