package com.mibdm.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.mibdm.exception.ApiException;
import com.mibdm.model.PorEscucharActualizar;
import com.mibdm.model.PorEscucharCrear;
import com.mibdm.model.PorEscucharVista;
import com.mibdm.repository.AlbumRepository;
import com.mibdm.repository.PorEscucharRepository;

@Service
public class PorEscucharService {

    private static final Set<String> PRIORIDADES = Set.of("alta", "media", "baja");
    private static final Set<String> ESTADOS = Set.of("pendiente", "escuchado", "descartado");

    private final PorEscucharRepository porEscucharRepository;
    private final AlbumRepository albumRepository;
    private final UsuarioService usuarioService;

    public PorEscucharService(PorEscucharRepository porEscucharRepository,
                              AlbumRepository albumRepository,
                              UsuarioService usuarioService) {
        this.porEscucharRepository = porEscucharRepository;
        this.albumRepository = albumRepository;
        this.usuarioService = usuarioService;
    }

    public List<PorEscucharVista> listar(int idUsuario) {
        usuarioService.exigir(idUsuario);
        return porEscucharRepository.listarPorUsuario(idUsuario);
    }

    public void agregar(int idUsuario, PorEscucharCrear datos) {
        usuarioService.exigir(idUsuario);
        if (datos == null) {
            throw ApiException.badRequest("Falta el cuerpo de la solicitud");
        }
        String prioridad = datos.prioridad() == null || datos.prioridad().isBlank() ? "media" : datos.prioridad();
        if (!PRIORIDADES.contains(prioridad)) {
            throw ApiException.badRequest("Prioridad invalida. Use: alta, media o baja");
        }
        if (!albumRepository.existe(datos.idAlbum())) {
            throw ApiException.notFound("No existe el album " + datos.idAlbum());
        }
        try {
            porEscucharRepository.agregar(idUsuario, datos.idAlbum(), prioridad);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw ApiException.conflict("Ese album ya esta en tu lista por escuchar");
        }
    }

    public void actualizar(int idUsuario, int idAlbum, PorEscucharActualizar datos) {
        usuarioService.exigir(idUsuario);
        if (datos == null || (datos.prioridad() == null && datos.estado() == null)) {
            throw ApiException.badRequest("Indique prioridad y/o estado");
        }
        if (datos.prioridad() != null && !PRIORIDADES.contains(datos.prioridad())) {
            throw ApiException.badRequest("Prioridad invalida. Use: alta, media o baja");
        }
        if (datos.estado() != null && !ESTADOS.contains(datos.estado())) {
            throw ApiException.badRequest("Estado invalido. Use: pendiente, escuchado o descartado");
        }
        if (porEscucharRepository.actualizar(idUsuario, idAlbum, datos.prioridad(), datos.estado()) == 0) {
            throw ApiException.notFound("Ese album no esta en tu lista por escuchar");
        }
    }

    public void eliminar(int idUsuario, int idAlbum) {
        usuarioService.exigir(idUsuario);
        if (porEscucharRepository.eliminar(idUsuario, idAlbum) == 0) {
            throw ApiException.notFound("Ese album no esta en tu lista por escuchar");
        }
    }
}
