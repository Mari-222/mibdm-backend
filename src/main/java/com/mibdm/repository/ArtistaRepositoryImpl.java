package com.mibdm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.mibdm.model.Artista;

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
}
