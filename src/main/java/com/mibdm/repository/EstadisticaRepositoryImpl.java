package com.mibdm.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.mibdm.model.Estadisticas.ItemConteo;

@Repository
public class EstadisticaRepositoryImpl implements EstadisticaRepository {

    private static final RowMapper<ItemConteo> MAPPER =
            (rs, i) -> new ItemConteo(rs.getString("nombre"), rs.getInt("total"));

    private final JdbcTemplate jdbc;

    public EstadisticaRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public int totalEscuchas(int idUsuario) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM escucha WHERE id_usuario = ?", Integer.class, idUsuario);
        return n == null ? 0 : n;
    }

    @Override
    public long totalSegundos(int idUsuario) {
        Long s = jdbc.queryForObject(
                "SELECT COALESCE(SUM(segundos_escuchados), 0) FROM escucha WHERE id_usuario = ?", Long.class, idUsuario);
        return s == null ? 0 : s;
    }

    @Override
    public List<ItemConteo> topCanciones(int idUsuario, int limite) {
        return jdbc.query("""
                SELECT c.titulo AS nombre, COUNT(*) AS total
                FROM escucha e
                JOIN cancion c ON c.id_cancion = e.id_cancion
                WHERE e.id_usuario = ?
                GROUP BY c.id_cancion, c.titulo
                ORDER BY total DESC, c.titulo
                LIMIT ?
                """, MAPPER, idUsuario, limite);
    }

    @Override
    public List<ItemConteo> topArtistas(int idUsuario, int limite) {
        // Escuchas de canciones (interpretes) + escuchas de albumes (artista del album)
        return jdbc.query("""
                SELECT nombre, COUNT(*) AS total
                FROM (
                    SELECT a.id_artista, a.nombre
                    FROM escucha e
                    JOIN cancion_artista ca ON ca.id_cancion = e.id_cancion AND ca.rol = 'interprete'
                    JOIN artista a ON a.id_artista = ca.id_artista
                    WHERE e.id_usuario = ?
                    UNION ALL
                    SELECT a.id_artista, a.nombre
                    FROM escucha e
                    JOIN album al ON al.id_album = e.id_album
                    JOIN artista a ON a.id_artista = al.id_artista
                    WHERE e.id_usuario = ?
                ) t
                GROUP BY id_artista, nombre
                ORDER BY total DESC, nombre
                LIMIT ?
                """, MAPPER, idUsuario, idUsuario, limite);
    }

    @Override
    public List<ItemConteo> topGeneros(int idUsuario, int limite) {
        return jdbc.query("""
                SELECT g.nombre AS nombre, COUNT(*) AS total
                FROM escucha e
                JOIN cancion_genero cg ON cg.id_cancion = e.id_cancion
                JOIN genero g ON g.id_genero = cg.id_genero
                WHERE e.id_usuario = ?
                GROUP BY g.id_genero, g.nombre
                ORDER BY total DESC, g.nombre
                LIMIT ?
                """, MAPPER, idUsuario, limite);
    }

    @Override
    public List<ItemConteo> porPlataforma(int idUsuario) {
        return jdbc.query("""
                SELECT p.nombre AS nombre, COUNT(*) AS total
                FROM escucha e
                JOIN plataforma p ON p.id_plataforma = e.id_plataforma
                WHERE e.id_usuario = ?
                GROUP BY p.id_plataforma, p.nombre
                ORDER BY total DESC, p.nombre
                """, MAPPER, idUsuario);
    }
}
