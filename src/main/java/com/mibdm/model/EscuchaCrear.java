package com.mibdm.model;

import java.time.LocalDateTime;

/** Cuerpo de POST /api/usuarios/{id}/escuchas. Debe venir idCancion o idAlbum, nunca ambos. */
public record EscuchaCrear(
        Integer idCancion,
        Integer idAlbum,
        int idPlataforma,
        Integer segundosEscuchados,
        LocalDateTime fechaHora) {
}
