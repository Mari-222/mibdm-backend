package com.mibdm.model;

/** Cuerpo de PUT /por-escuchar/{idAlbum}. Se puede enviar solo uno de los dos campos. */
public record PorEscucharActualizar(String prioridad, String estado) {
}
