package com.mibdm.repository;

import java.util.List;
import java.util.Optional;

import com.mibdm.model.Artista;

public interface ArtistaRepository {

    List<Artista> listar(String q);

    Optional<Artista> buscarPorId(int idArtista);

    boolean existe(int idArtista);
}
