package com.mibdm.model;

/** artistas = interpretes separados por coma; generos = generos separados por coma. */
public record Cancion(
        int idCancion,
        String titulo,
        int duracionSeg,
        Integer numeroPista,
        Integer idAlbum,
        String album,
        String artistas,
        String generos) {
}
