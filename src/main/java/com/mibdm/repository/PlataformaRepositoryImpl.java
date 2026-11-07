package com.mibdm.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mibdm.model.Plataforma;

@Repository
public class PlataformaRepositoryImpl implements PlataformaRepository {

    private final JdbcTemplate jdbc;

    public PlataformaRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Plataforma> listar() {
        return jdbc.query("SELECT id_plataforma, nombre, tipo FROM plataforma ORDER BY nombre",
                (rs, i) -> new Plataforma(rs.getInt("id_plataforma"), rs.getString("nombre"), rs.getString("tipo")));
    }

    @Override
    public boolean existe(int idPlataforma) {
        Integer n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM plataforma WHERE id_plataforma = ?", Integer.class, idPlataforma);
        return n != null && n > 0;
    }
}
