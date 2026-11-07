package com.mibdm.model;

import java.time.LocalDate;

public record PorEscucharVista(
        int idAlbum,
        String titulo,
        String artista,
        LocalDate fechaAgregado,
        String prioridad,
        String estado) {
}
