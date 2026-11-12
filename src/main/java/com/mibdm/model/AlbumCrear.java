package com.mibdm.model;

/**
 * Cuerpo de POST /api/albumes. Solo lo puede usar un usuario de tipo 'artista'.
 * El album queda a nombre del perfil de artista de ese usuario (no se envia idArtista).
 */
public record AlbumCrear(int idUsuario, String titulo, int anioLanzamiento, String tipo) {
}
