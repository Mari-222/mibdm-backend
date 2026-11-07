package com.mibdm.model;

/** Cuerpo de POST /api/usuarios (registro de un usuario normal). */
public record UsuarioCrear(String nombre, String apellido, String correo, String pais) {
}
