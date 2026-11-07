package com.mibdm.model;

import java.util.List;

/**
 * Cuerpo de POST /api/canciones. Solo lo puede usar un usuario de tipo 'artista'.
 * idAlbum y numeroPista son opcionales (un sencillo no pertenece a un album).
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
