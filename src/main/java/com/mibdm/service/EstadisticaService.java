package com.mibdm.service;

import org.springframework.stereotype.Service;

import com.mibdm.model.Estadisticas;
import com.mibdm.repository.EstadisticaRepository;

@Service
public class EstadisticaService {

    private static final int TOP = 5;

    private final EstadisticaRepository estadisticaRepository;
    private final UsuarioService usuarioService;

    public EstadisticaService(EstadisticaRepository estadisticaRepository, UsuarioService usuarioService) {
        this.estadisticaRepository = estadisticaRepository;
        this.usuarioService = usuarioService;
    }

    public Estadisticas resumen(int idUsuario) {
        usuarioService.exigir(idUsuario);
        return new Estadisticas(
                estadisticaRepository.totalEscuchas(idUsuario),
                estadisticaRepository.totalSegundos(idUsuario),
                estadisticaRepository.topCanciones(idUsuario, TOP),
                estadisticaRepository.topArtistas(idUsuario, TOP),
                estadisticaRepository.topGeneros(idUsuario, TOP),
                estadisticaRepository.porPlataforma(idUsuario));
    }
}
