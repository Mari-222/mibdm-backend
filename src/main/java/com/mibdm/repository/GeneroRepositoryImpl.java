package com.mibdm.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mibdm.model.Genero;

@Repository
public class GeneroRepositoryImpl implements GeneroRepository {

    private final JdbcTemplate jdbc;

    public GeneroRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Genero> listar() {
        return jdbc.query("SELECT id_genero, nombre FROM genero ORDER BY nombre",
                (rs, i) -> new Genero(rs.getInt("id_genero"), rs.getString("nombre")));
    }

    @Override
    public boolean existe(int idGenero) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM genero WHERE id_genero = ?", Integer.class, idGenero);
        return n != null && n > 0;
    }
}
