package com.mibdm.repository;

import java.util.List;

import com.mibdm.model.PorEscucharVista;

public interface PorEscucharRepository {

    List<PorEscucharVista> listarPorUsuario(int idUsuario);

    /** Lanza DuplicateKeyException si el album ya esta en la lista del usuario. */
    void agregar(int idUsuario, int idAlbum, String prioridad);

    /** Los campos null no se modifican. Devuelve las filas afectadas. */
    int actualizar(int idUsuario, int idAlbum, String prioridad, String estado);

    int eliminar(int idUsuario, int idAlbum);
}
