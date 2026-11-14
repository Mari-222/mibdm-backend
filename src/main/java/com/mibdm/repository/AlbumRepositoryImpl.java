package com.mibdm.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.mibdm.model.Album;
import com.mibdm.model.AlbumCrear;

@Repository
public class AlbumRepositoryImpl implements AlbumRepository {

    private static final String SELECT = """
            SELECT al.id_album, al.titulo, al.anio_lanzamiento, al.tipo, al.id_artista,
                   a.nombre AS artista,
                   (SELECT COUNT(*) FROM cancion c WHERE c.id_album = al.id_album) AS num_canciones
            FROM album al
            JOIN artista a ON a.id_artista = al.id_artista
            """;

    private static final RowMapper<Album> MAPPER = (rs, i) -> new Album(
            rs.getInt("id_album"),
            rs.getString("titulo"),
            rs.getInt("anio_lanzamiento"),
            rs.getString("tipo"),
            rs.getInt("id_artista"),
            rs.getString("artista"),
            rs.getInt("num_canciones"));

    private final JdbcTemplate jdbc;

    public AlbumRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Album> listar(String q) {
        String patron = "%" + (q == null ? "" : q.trim()) + "%";
        return jdbc.query(SELECT + "WHERE al.titulo LIKE ? ORDER BY al.titulo", MAPPER, patron);
    }

    @Override
    public List<Album> listarPorArtista(int idArtista) {
        return jdbc.query(SELECT + "WHERE al.id_artista = ? ORDER BY al.anio_lanzamiento", MAPPER, idArtista);
    }

    @Override
    public Optional<Album> buscarPorId(int idAlbum) {
        return jdbc.query(SELECT + "WHERE al.id_album = ?", MAPPER, idAlbum).stream().findFirst();
    }

    @Override
    public boolean existe(int idAlbum) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM album WHERE id_album = ?", Integer.class, idAlbum);
        return n != null && n > 0;
    }

    @Override
    public int crear(int idArtista, AlbumCrear a) {
        String sql = "INSERT INTO album (titulo, anio_lanzamiento, tipo, id_artista) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, a.titulo());
            ps.setInt(2, a.anioLanzamiento());
            ps.setString(3, a.tipo());
            ps.setInt(4, idArtista);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public Double promedioCalificaciones(int idAlbum) {
        return jdbc.queryForObject(
                "SELECT AVG(puntuacion) FROM calificacion_album WHERE id_album = ?", Double.class, idAlbum);
    }

    @Override
    public int totalCalificaciones(int idAlbum) {
        Integer n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM calificacion_album WHERE id_album = ?", Integer.class, idAlbum);
        return n == null ? 0 : n;
    }
}
