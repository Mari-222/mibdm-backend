package com.mibdm.repository;

import java.util.List;

import com.mibdm.model.CalificacionVista;

public interface CalificacionRepository {

    List<CalificacionVista> listarCanciones(int idUsuario);

    /** Crea la calificacion o, si ya existe, la actualiza (un usuario califica una cancion una sola vez). */
    void guardarCancion(int idUsuario, int idCancion, int puntuacion, String comentario);

    int eliminarCancion(int idUsuario, int idCancion);

    List<CalificacionVista> listarAlbumes(int idUsuario);

    void guardarAlbum(int idUsuario, int idAlbum, int puntuacion, String comentario);

    int eliminarAlbum(int idUsuario, int idAlbum);
}
