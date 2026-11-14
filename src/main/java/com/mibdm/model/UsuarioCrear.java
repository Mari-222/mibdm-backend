package com.mibdm.model;

/**
 * Cuerpo de POST /api/usuarios (registro).
 * tipo es "normal" o "artista" (si no viene, se asume "normal"). Si es "artista" debe venir
 * el objeto artista, con el que se crea su perfil de artista junto con la cuenta.
 */
public record UsuarioCrear(String nombre, String apellido, String correo, String pais,
                           String tipo, ArtistaDatos artista) {
}
