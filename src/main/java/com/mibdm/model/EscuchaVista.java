package com.mibdm.model;

import java.time.LocalDateTime;

/** tipoItem es 'cancion' o 'album' segun lo que se escucho. */
public record EscuchaVista(
        int idEscucha,
        LocalDateTime fechaHora,
        int segundosEscuchados,
        String tipoItem,
        Integer idCancion,
        Integer idAlbum,
        String titulo,
        String artista,
        int idPlataforma,
        String plataforma) {
}
