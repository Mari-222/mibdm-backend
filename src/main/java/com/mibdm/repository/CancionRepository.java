package com.mibdm.repository;

import java.util.List;
import java.util.Optional;

import com.mibdm.model.Cancion;
import com.mibdm.model.CancionDetalle.Participante;
import com.mibdm.model.Genero;

public interface CancionRepository {

    /** Filtra por titulo (q) y, opcionalmente, por genero. Ambos pueden ser null. */
    List<Cancion> listar(String q, Integer idGenero);

    List<Cancion> listarPorAlbum(int idAlbum);

    Optional<Cancion> buscarPorId(int idCancion);

    boolean existe(int idCancion);

    List<Participante> participantes(int idCancion);

    List<Genero> generos(int idCancion);

    Double promedioCalificaciones(int idCancion);

    int totalCalificaciones(int idCancion);

    /** Inserta la cancion y devuelve el id generado. */
    int crear(String titulo, int duracionSeg, Integer numeroPista, Integer idAlbum);

    void agregarArtista(int idCancion, int idArtista, String rol);

    void agregarGenero(int idCancion, int idGenero);
}
