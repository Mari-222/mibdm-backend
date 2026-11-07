package com.mibdm.model;

import java.time.LocalDate;

/** Sirve para calificaciones de canciones y de albumes (idItem es el id de uno u otro). */
public record CalificacionVista(int idItem, String titulo, int puntuacion, LocalDate fecha, String comentario) {
}
