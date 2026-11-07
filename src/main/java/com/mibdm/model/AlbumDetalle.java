package com.mibdm.model;

import java.util.List;

public record AlbumDetalle(Album album, List<Cancion> canciones, Double promedio, int totalCalificaciones) {
}
