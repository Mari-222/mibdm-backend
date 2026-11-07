package com.mibdm.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.mibdm.model.PorEscucharVista;

@Repository
public class PorEscucharRepositoryImpl implements PorEscucharRepository {

    private static final RowMapper<PorEscucharVista> MAPPER = (rs, i) -> new PorEscucharVista(
            rs.getInt("id_album"),
            rs.getString("titulo"),
            rs.getString("artista"),
            rs.getDate("fecha_agregado").toLocalDate(),
            rs.getString("prioridad"),
            rs.getString("estado"));

    private final JdbcTemplate jdbc;

    public PorEscucharRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<PorEscucharVista> listarPorUsuario(int idUsuario) {
        return jdbc.query("""
                SELECT pe.id_album, al.titulo, a.nombre AS artista,
                       pe.fecha_agregado, pe.prioridad, pe.estado
                FROM por_escuchar pe
                JOIN album al ON al.id_album = pe.id_album
                JOIN artista a ON a.id_artista = al.id_artista
                WHERE pe.id_usuario = ?
                ORDER BY FIELD(pe.estado, 'pendiente', 'escuchado', 'descartado'),
                         FIELD(pe.prioridad, 'alta', 'media', 'baja'),
                         pe.fecha_agregado
                """, MAPPER, idUsuario);
    }

    @Override
    public void agregar(int idUsuario, int idAlbum, String prioridad) {
        jdbc.update("""
                INSERT INTO por_escuchar (id_usuario, id_album, fecha_agregado, prioridad)
                VALUES (?, ?, CURRENT_DATE, ?)
                """, idUsuario, idAlbum, prioridad);
    }

    @Override
    public int actualizar(int idUsuario, int idAlbum, String prioridad, String estado) {
        // Solo se incluyen en el SET los campos que llegaron
        List<String> cambios = new ArrayList<>();
        List<Object> args = new ArrayList<>();
        if (prioridad != null) {
            cambios.add("prioridad = ?");
            args.add(prioridad);
        }
        if (estado != null) {
            cambios.add("estado = ?");
            args.add(estado);
        }
        if (cambios.isEmpty()) {
            return 0;
        }
        args.add(idUsuario);
        args.add(idAlbum);
        return jdbc.update("UPDATE por_escuchar SET " + String.join(", ", cambios)
                + " WHERE id_usuario = ? AND id_album = ?", args.toArray());
    }

    @Override
    public int eliminar(int idUsuario, int idAlbum) {
        return jdbc.update("DELETE FROM por_escuchar WHERE id_usuario = ? AND id_album = ?", idUsuario, idAlbum);
    }
}
