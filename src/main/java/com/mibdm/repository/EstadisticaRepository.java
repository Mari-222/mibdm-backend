package com.mibdm.repository;

import java.util.List;

import com.mibdm.model.Estadisticas.ItemConteo;

public interface EstadisticaRepository {

    int totalEscuchas(int idUsuario);

    long totalSegundos(int idUsuario);

    List<ItemConteo> topCanciones(int idUsuario, int limite);

    List<ItemConteo> topArtistas(int idUsuario, int limite);

    List<ItemConteo> topGeneros(int idUsuario, int limite);

    List<ItemConteo> porPlataforma(int idUsuario);
}
