package com.mibdm.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.mibdm.model.Cancion;
import com.mibdm.model.CancionDetalle.Participante;
import com.mibdm.model.Genero;

@Repository
public class CancionRepositoryImpl implements CancionRepository {

    private static final String SELECT = """
            SELECT c.id_cancion, c.titulo, c.duracion_seg, c.numero_pista, c.id_album,
                   al.titulo AS album,
                   (SELECT GROUP_CONCAT(a.nombre ORDER BY a.nombre SEPARATOR ', ')
                      FROM cancion_artista ca
                      JOIN artista a ON a.id_artista = ca.id_artista
                     WHERE ca.id_cancion = c.id_cancion AND ca.rol = 'interprete') AS artistas,
                   (SELECT GROUP_CONCAT(g.nombre ORDER BY g.nombre SEPARATOR ', ')
                      FROM cancion_genero cg
                      JOIN genero g ON g.id_genero = cg.id_genero
                     WHERE cg.id_cancion = c.id_cancion) AS generos
            FROM cancion c
            LEFT JOIN album al ON al.id_album = c.id_album
            """;

    private static final RowMapper<Cancion> MAPPER = (rs, i) -> new Cancion(
            rs.getInt("id_cancion"),
            rs.getString("titulo"),
            rs.getInt("duracion_seg"),
            rs.getObject("numero_pista", Integer.class),
            rs.getObject("id_album", Integer.class),
            rs.getString("album"),
            rs.getString("artistas"),
            rs.getString("generos"));

    private final JdbcTemplate jdbc;

    public CancionRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Cancion> listar(String q, Integer idGenero) {
        StringBuilder sql = new StringBuilder(SELECT).append("WHERE c.titulo LIKE ? ");
        List<Object> args = new ArrayList<>();
        args.add("%" + (q == null ? "" : q.trim()) + "%");
        if (idGenero != null) {
            sql.append("AND EXISTS (SELECT 1 FROM cancion_genero cg ")
               .append("WHERE cg.id_cancion = c.id_cancion AND cg.id_genero = ?) ");
            args.add(idGenero);
        }
        sql.append("ORDER BY c.titulo");
        return jdbc.query(sql.toString(), MAPPER, args.toArray());
    }

    @Override
    public List<Cancion> listarPorAlbum(int idAlbum) {
        return jdbc.query(SELECT + "WHERE c.id_album = ? ORDER BY c.numero_pista", MAPPER, idAlbum);
    }

    @Override
    public Optional<Cancion> buscarPorId(int idCancion) {
        return jdbc.query(SELECT + "WHERE c.id_cancion = ?", MAPPER, idCancion).stream().findFirst();
    }

    @Override
    public boolean existe(int idCancion) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM cancion WHERE id_cancion = ?", Integer.class, idCancion);
        return n != null && n > 0;
    }

    @Override
    public List<Participante> participantes(int idCancion) {
        return jdbc.query("""
                SELECT a.id_artista, a.nombre, ca.rol
                FROM cancion_artista ca
                JOIN artista a ON a.id_artista = ca.id_artista
                WHERE ca.id_cancion = ?
                ORDER BY ca.rol, a.nombre
                """,
                (rs, i) -> new Participante(rs.getInt("id_artista"), rs.getString("nombre"), rs.getString("rol")),
                idCancion);
    }

    @Override
    public List<Genero> generos(int idCancion) {
        return jdbc.query("""
                SELECT g.id_genero, g.nombre
                FROM cancion_genero cg
                JOIN genero g ON g.id_genero = cg.id_genero
                WHERE cg.id_cancion = ?
                ORDER BY g.nombre
                """,
                (rs, i) -> new Genero(rs.getInt("id_genero"), rs.getString("nombre")),
                idCancion);
    }

    @Override
    public Double promedioCalificaciones(int idCancion) {
        return jdbc.queryForObject(
                "SELECT AVG(puntuacion) FROM calificacion WHERE id_cancion = ?", Double.class, idCancion);
    }

    @Override
    public int totalCalificaciones(int idCancion) {
        Integer n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM calificacion WHERE id_cancion = ?", Integer.class, idCancion);
        return n == null ? 0 : n;
    }

    @Override
    public int crear(String titulo, int duracionSeg, Integer numeroPista, Integer idAlbum) {
        String sql = "INSERT INTO cancion (titulo, duracion_seg, numero_pista, id_album) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, titulo);
            ps.setInt(2, duracionSeg);
            if (numeroPista == null) {
                ps.setNull(3, Types.SMALLINT);
            } else {
                ps.setInt(3, numeroPista);
            }
            if (idAlbum == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, idAlbum);
            }
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public void agregarArtista(int idCancion, int idArtista, String rol) {
        jdbc.update("INSERT INTO cancion_artista (id_cancion, id_artista, rol) VALUES (?, ?, ?)",
                idCancion, idArtista, rol);
    }

    @Override
    public void agregarGenero(int idCancion, int idGenero) {
        jdbc.update("INSERT INTO cancion_genero (id_cancion, id_genero) VALUES (?, ?)", idCancion, idGenero);
    }
}
