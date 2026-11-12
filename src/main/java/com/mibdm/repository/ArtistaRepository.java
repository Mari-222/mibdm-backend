package com.mibdm.repository;

import java.util.List;
import java.util.Optional;

import com.mibdm.model.Artista;
import com.mibdm.model.ArtistaDatos;

public interface ArtistaRepository {

    List<Artista> listar(String q);

    Optional<Artista> buscarPorId(int idArtista);

    boolean existe(int idArtista);

    /** True si ya hay un artista con ese nombre (sin distinguir mayusculas), ignorando a excluirId (puede ser null). */
    boolean existePorNombre(String nombre, Integer excluirId);

    /** Inserta un artista y devuelve el id generado. */
    int crear(ArtistaDatos artista);

    void actualizar(int idArtista, ArtistaDatos artista);
}
