package com.mibdm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mibdm.exception.ApiException;
import com.mibdm.model.CalificacionCrear;
import com.mibdm.model.CalificacionVista;
import com.mibdm.repository.AlbumRepository;
import com.mibdm.repository.CalificacionRepository;
import com.mibdm.repository.CancionRepository;

@Service
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final CancionRepository cancionRepository;
    private final AlbumRepository albumRepository;
    private final UsuarioService usuarioService;

    public CalificacionService(CalificacionRepository calificacionRepository,
                               CancionRepository cancionRepository,
                               AlbumRepository albumRepository,
                               UsuarioService usuarioService) {
        this.calificacionRepository = calificacionRepository;
        this.cancionRepository = cancionRepository;
        this.albumRepository = albumRepository;
        this.usuarioService = usuarioService;
    }

    // ---------- canciones ----------

    public List<CalificacionVista> listarCanciones(int idUsuario) {
        usuarioService.exigir(idUsuario);
        return calificacionRepository.listarCanciones(idUsuario);
    }

    public void calificarCancion(int idUsuario, int idCancion, CalificacionCrear datos) {
        usuarioService.exigir(idUsuario);
        validar(datos);
        if (!cancionRepository.existe(idCancion)) {
            throw ApiException.notFound("No existe la cancion " + idCancion);
        }
        calificacionRepository.guardarCancion(idUsuario, idCancion, datos.puntuacion(), limpiar(datos.comentario()));
    }

    public void eliminarCancion(int idUsuario, int idCancion) {
        usuarioService.exigir(idUsuario);
        if (calificacionRepository.eliminarCancion(idUsuario, idCancion) == 0) {
            throw ApiException.notFound("El usuario no ha calificado esa cancion");
        }
    }

    // ---------- albumes ----------

    public List<CalificacionVista> listarAlbumes(int idUsuario) {
        usuarioService.exigir(idUsuario);
        return calificacionRepository.listarAlbumes(idUsuario);
    }

    public void calificarAlbum(int idUsuario, int idAlbum, CalificacionCrear datos) {
        usuarioService.exigir(idUsuario);
        validar(datos);
        if (!albumRepository.existe(idAlbum)) {
            throw ApiException.notFound("No existe el album " + idAlbum);
        }
        calificacionRepository.guardarAlbum(idUsuario, idAlbum, datos.puntuacion(), limpiar(datos.comentario()));
    }

    public void eliminarAlbum(int idUsuario, int idAlbum) {
        usuarioService.exigir(idUsuario);
        if (calificacionRepository.eliminarAlbum(idUsuario, idAlbum) == 0) {
            throw ApiException.notFound("El usuario no ha calificado ese album");
        }
    }

    // ---------- validaciones ----------

    private static void validar(CalificacionCrear datos) {
        if (datos == null) {
            throw ApiException.badRequest("Falta el cuerpo de la solicitud");
        }
        if (datos.puntuacion() < 1 || datos.puntuacion() > 5) {
            throw ApiException.badRequest("La puntuacion debe estar entre 1 y 5");
        }
        if (datos.comentario() != null && datos.comentario().length() > 255) {
            throw ApiException.badRequest("El comentario no puede superar 255 caracteres");
        }
    }

    private static String limpiar(String comentario) {
        return comentario == null || comentario.isBlank() ? null : comentario.trim();
    }
}
