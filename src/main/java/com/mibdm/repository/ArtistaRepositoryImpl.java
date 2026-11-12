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

import com.mibdm.model.Artista;
import com.mibdm.model.ArtistaDatos;

@Repository
public class ArtistaRepositoryImpl implements ArtistaRepository {

    private static final RowMapper<Artista> MAPPER = (rs, i) -> new Artista(
            rs.getInt("id_artista"),
            rs.getString("nombre"),
            rs.getString("pais_origen"),
            rs.getString("tipo"));

    private final JdbcTemplate jdbc;

    public ArtistaRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Artista> listar(String q) {
        String patron = "%" + (q == null ? "" : q.trim()) + "%";
        return jdbc.query(
                "SELECT id_artista, nombre, pais_origen, tipo FROM artista WHERE nombre LIKE ? ORDER BY nombre",
                MAPPER, patron);
    }

    @Override
    public Optional<Artista> buscarPorId(int idArtista) {
        return jdbc.query(
                "SELECT id_artista, nombre, pais_origen, tipo FROM artista WHERE id_artista = ?",
                MAPPER, idArtista).stream().findFirst();
    }

    @Override
    public boolean existe(int idArtista) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM artista WHERE id_artista = ?", Integer.class, idArtista);
        return n != null && n > 0;
    }

    @Override
    public boolean existePorNombre(String nombre, Integer excluirId) {
        Integer n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM artista WHERE LOWER(nombre) = LOWER(?) AND id_artista <> ?",
                Integer.class, nombre, excluirId == null ? -1 : excluirId);
        return n != null && n > 0;
    }

    @Override
    public int crear(ArtistaDatos a) {
        String sql = "INSERT INTO artista (nombre, pais_origen, tipo) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, a.nombre());
            ps.setString(2, a.paisOrigen());
            ps.setString(3, a.tipo());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public void actualizar(int idArtista, ArtistaDatos a) {
        jdbc.update("UPDATE artista SET nombre = ?, pais_origen = ?, tipo = ? WHERE id_artista = ?",
                a.nombre(), a.paisOrigen(), a.tipo(), idArtista);
    }
}
