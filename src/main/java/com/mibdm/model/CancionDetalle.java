package com.mibdm.model;

import java.util.List;

public record CancionDetalle(
        Cancion cancion,
        List<Participante> participantes,
        List<Genero> generos,
        Double promedio,
        int totalCalificaciones) {

    /** Artista que participa en la cancion con un rol (interprete, compositor, productor). */
    public record Participante(int idArtista, String nombre, String rol) {
    }
}
