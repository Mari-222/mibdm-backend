package com.mibdm.model;

import java.util.List;

public record Estadisticas(
        int totalEscuchas,
        long totalSegundos,
        List<ItemConteo> topCanciones,
        List<ItemConteo> topArtistas,
        List<ItemConteo> topAlbumes,
        List<ItemConteo> topGeneros,
        List<ItemConteo> porPlataforma) {

    public record ItemConteo(String nombre, int total) {
    }
}
