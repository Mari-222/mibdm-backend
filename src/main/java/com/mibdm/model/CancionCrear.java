package com.mibdm.model;

import java.util.List;

/**
 * Cuerpo de POST /api/canciones. Solo lo puede usar un usuario de tipo 'artista'.
 * idAlbum y numeroPista son opcionales (un sencillo no pertenece a un album).
 * La cancion queda a nombre del artista del usuario (compositor e interprete). idsInterpretes
 * son los OTROS artistas que participan como interpretes; puede ir vacio o null.
 */
public record CancionCrear(
        int idUsuario,
        String titulo,
        int duracionSeg,
        Integer numeroPista,
        Integer idAlbum,
        List<Integer> idsInterpretes,
        List<Integer> idsGeneros) {
}
