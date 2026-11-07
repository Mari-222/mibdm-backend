package com.mibdm.model;

import java.time.LocalDate;

public record Usuario(
        int idUsuario,
        String nombre,
        String apellido,
        String correo,
        String pais,
        LocalDate fechaRegistro,
        String tipo) {
}
