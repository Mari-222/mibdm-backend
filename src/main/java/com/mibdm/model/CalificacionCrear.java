package com.mibdm.model;

/** Cuerpo de PUT de calificaciones: puntuacion de 1 a 5 y comentario opcional. */
public record CalificacionCrear(int puntuacion, String comentario) {
}
