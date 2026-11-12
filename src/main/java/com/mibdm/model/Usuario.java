package com.mibdm.model;

import java.time.LocalDate;

/** idArtista es el perfil de artista del usuario tipo 'artista'; es null para los usuarios normales. */
public record Usuario(
        int idUsuario,
        String nombre,
        String apellido,
        String correo,
        String pais,
        LocalDate fechaRegistro,
        String tipo,
        Integer idArtista) {
}
