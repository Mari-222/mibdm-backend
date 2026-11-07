package com.mibdm.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.mibdm.model.Usuario;
import com.mibdm.model.UsuarioCrear;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private static final RowMapper<Usuario> MAPPER = (rs, i) -> new Usuario(
            rs.getInt("id_usuario"),
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("correo"),
            rs.getString("pais"),
            rs.getDate("fecha_registro").toLocalDate(),
            rs.getString("tipo"));

    private static final String SELECT =
            "SELECT id_usuario, nombre, apellido, correo, pais, fecha_registro, tipo FROM usuario ";

    private final JdbcTemplate jdbc;

    public UsuarioRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Usuario> buscarPorId(int idUsuario) {
        return jdbc.query(SELECT + "WHERE id_usuario = ?", MAPPER, idUsuario).stream().findFirst();
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return jdbc.query(SELECT + "WHERE correo = ?", MAPPER, correo).stream().findFirst();
    }

    @Override
    public int crear(UsuarioCrear u) {
        String sql = "INSERT INTO usuario (nombre, apellido, correo, pais) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, u.nombre());
            ps.setString(2, u.apellido());
            ps.setString(3, u.correo());
            ps.setString(4, u.pais());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }
}
