package com.mibdm.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.mibdm.model.EscuchaVista;

public interface EscuchaRepository {

    List<EscuchaVista> listarPorUsuario(int idUsuario, int limite);

    /** Inserta una escucha de cancion (idAlbum null) o de album (idCancion null). Devuelve el id generado. */
    int crear(int idUsuario, Integer idCancion, Integer idAlbum, int idPlataforma,
              int segundosEscuchados, LocalDateTime fechaHora);

    /** Devuelve cuantas filas se borraron (0 si la escucha no es de ese usuario). */
    int eliminar(int idUsuario, int idEscucha);

    /** Duracion total de la cancion o, para un album, la suma de sus canciones. */
    int duracionCancion(int idCancion);

    int duracionAlbum(int idAlbum);
}
