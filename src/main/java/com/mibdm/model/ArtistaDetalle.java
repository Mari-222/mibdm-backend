package com.mibdm.model;

import java.util.List;

public record ArtistaDetalle(Artista artista, List<Album> albumes) {
}
