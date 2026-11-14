package com.mibdm.repository;

import java.util.List;
import java.util.Optional;

import com.mibdm.model.Album;
import com.mibdm.model.AlbumCrear;

public interface AlbumRepository {

    List<Album> listar(String q);

    List<Album> listarPorArtista(int idArtista);

    Optional<Album> buscarPorId(int idAlbum);

    boolean existe(int idAlbum);

    /** True si el artista ya tiene un album con ese titulo (sin distinguir mayusculas), ignorando a excluirId (puede ser null). */
    boolean existeTituloDeArtista(int idArtista, String titulo, Integer excluirId);

    /** Inserta el album a nombre del artista indicado y devuelve el id generado. */
    int crear(int idArtista, AlbumCrear album);

    Double promedioCalificaciones(int idAlbum);

    int totalCalificaciones(int idAlbum);
}