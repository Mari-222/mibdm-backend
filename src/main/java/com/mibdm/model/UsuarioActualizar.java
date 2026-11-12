package com.mibdm.model;

/**
 * Cuerpo de PUT /api/usuarios/{id} (modificar perfil). El tipo de cuenta no se puede cambiar.
 * artista solo se usa si la cuenta es de tipo artista (actualiza su perfil de artista).
 */
public record UsuarioActualizar(String nombre, String apellido, String correo, String pais,
                                ArtistaDatos artista) {
}
