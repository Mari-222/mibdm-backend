package com.mibdm.model;

/** numCanciones corresponde al atributo derivado num_canciones() del diagrama. */
public record Album(
        int idAlbum,
        String titulo,
        int anioLanzamiento,
        String tipo,
        int idArtista,
        String artista,
        int numCanciones) {
}
