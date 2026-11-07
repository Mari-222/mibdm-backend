package com.mibdm.model;

/** Cuerpo de POST /api/albumes. Solo lo puede usar un usuario de tipo 'artista'. */
public record AlbumCrear(int idUsuario, String titulo, int anioLanzamiento, String tipo, int idArtista) {
}
