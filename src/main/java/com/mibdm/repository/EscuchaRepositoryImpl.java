package com.mibdm.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.mibdm.model.EscuchaVista;

@Repository
public class EscuchaRepositoryImpl implements EscuchaRepository {

    private static final String SELECT = """
            SELECT e.id_escucha, e.fecha_hora, e.segundos_escuchados,
                   e.id_cancion, e.id_album, e.id_plataforma,
                   CASE WHEN e.id_cancion IS NOT NULL THEN 'cancion' ELSE 'album' END AS tipo_item,
                   COALESCE(c.titulo, al.titulo) AS titulo,
                   COALESCE(
                       (SELECT GROUP_CONCAT(a.nombre ORDER BY a.nombre SEPARATOR ', ')
                          FROM cancion_artista ca
                          JOIN artista a ON a.id_artista = ca.id_artista
                         WHERE ca.id_cancion = e.id_cancion AND ca.rol = 'interprete'),
                       aa.nombre) AS artista,
                   p.nombre AS plataforma
            FROM escucha e
            JOIN plataforma p ON p.id_plataforma = e.id_plataforma
            LEFT JOIN cancion c ON c.id_cancion = e.id_cancion
            LEFT JOIN album al ON al.id_album = e.id_album
            LEFT JOIN artista aa ON aa.id_artista = al.id_artista
            """;

    private static final RowMapper<EscuchaVista> MAPPER = (rs, i) -> new EscuchaVista(
            rs.getInt("id_escucha"),
            rs.getTimestamp("fecha_hora").toLocalDateTime(),
            rs.getInt("segundos_escuchados"),
            rs.getString("tipo_item"),
            rs.getObject("id_cancion", Integer.class),
            rs.getObject("id_album", Integer.class),
            rs.getString("titulo"),
            rs.getString("artista"),
            rs.getInt("id_plataforma"),
            rs.getString("plataforma"));

    private final JdbcTemplate jdbc;

    public EscuchaRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<EscuchaVista> listarPorUsuario(int idUsuario, int limite) {
        return jdbc.query(SELECT + "WHERE e.id_usuario = ? ORDER BY e.fecha_hora DESC, e.id_escucha DESC LIMIT ?",
                MAPPER, idUsuario, limite);
    }

    @Override
    public int crear(int idUsuario, Integer idCancion, Integer idAlbum, int idPlataforma,
                     int segundosEscuchados, LocalDateTime fechaHora) {
        String sql = """
                INSERT INTO escucha (fecha_hora, segundos_escuchados, id_usuario, id_cancion, id_album, id_plataforma)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, Timestamp.valueOf(fechaHora));
            ps.setInt(2, segundosEscuchados);
            ps.setInt(3, idUsuario);
            if (idCancion == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, idCancion);
            }
            if (idAlbum == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, idAlbum);
            }
            ps.setInt(6, idPlataforma);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public int eliminar(int idUsuario, int idEscucha) {
        return jdbc.update("DELETE FROM escucha WHERE id_escucha = ? AND id_usuario = ?", idEscucha, idUsuario);
    }

    @Override
    public int duracionCancion(int idCancion) {
        Integer d = jdbc.queryForObject(
                "SELECT COALESCE(SUM(duracion_seg), 0) FROM cancion WHERE id_cancion = ?", Integer.class, idCancion);
        return d == null ? 0 : d;
    }

    @Override
    public int duracionAlbum(int idAlbum) {
        Integer d = jdbc.queryForObject(
                "SELECT COALESCE(SUM(duracion_seg), 0) FROM cancion WHERE id_album = ?", Integer.class, idAlbum);
        return d == null ? 0 : d;
    }
}
