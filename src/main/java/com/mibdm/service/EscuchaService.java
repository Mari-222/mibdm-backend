package com.mibdm.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mibdm.exception.ApiException;
import com.mibdm.model.EscuchaCrear;
import com.mibdm.model.EscuchaVista;
import com.mibdm.repository.AlbumRepository;
import com.mibdm.repository.CancionRepository;
import com.mibdm.repository.EscuchaRepository;
import com.mibdm.repository.PlataformaRepository;

@Service
public class EscuchaService {

    private final EscuchaRepository escuchaRepository;
    private final CancionRepository cancionRepository;
    private final AlbumRepository albumRepository;
    private final PlataformaRepository plataformaRepository;
    private final UsuarioService usuarioService;

    public EscuchaService(EscuchaRepository escuchaRepository,
                          CancionRepository cancionRepository,
                          AlbumRepository albumRepository,
                          PlataformaRepository plataformaRepository,
                          UsuarioService usuarioService) {
        this.escuchaRepository = escuchaRepository;
        this.cancionRepository = cancionRepository;
        this.albumRepository = albumRepository;
        this.plataformaRepository = plataformaRepository;
        this.usuarioService = usuarioService;
    }

    public List<EscuchaVista> listar(int idUsuario, int limite) {
        usuarioService.exigir(idUsuario);
        int lim = Math.min(Math.max(limite, 1), 500);
        return escuchaRepository.listarPorUsuario(idUsuario, lim);
    }

    public EscuchaVista registrar(int idUsuario, EscuchaCrear datos) {
        usuarioService.exigir(idUsuario);
        if (datos == null) {
            throw ApiException.badRequest("Falta el cuerpo de la solicitud");
        }
        boolean hayCancion = datos.idCancion() != null;
        boolean hayAlbum = datos.idAlbum() != null;
        if (hayCancion == hayAlbum) {
            throw ApiException.badRequest("Debe indicar idCancion o idAlbum (solo uno de los dos)");
        }
        if (!plataformaRepository.existe(datos.idPlataforma())) {
            throw ApiException.notFound("No existe la plataforma " + datos.idPlataforma());
        }
        int duracion;
        if (hayCancion) {
            if (!cancionRepository.existe(datos.idCancion())) {
                throw ApiException.notFound("No existe la cancion " + datos.idCancion());
            }
            duracion = escuchaRepository.duracionCancion(datos.idCancion());
        } else {
            if (!albumRepository.existe(datos.idAlbum())) {
                throw ApiException.notFound("No existe el album " + datos.idAlbum());
            }
            duracion = escuchaRepository.duracionAlbum(datos.idAlbum());
        }
        // Si no se indican los segundos se asume que se escucho completo
        int segundos = datos.segundosEscuchados() == null ? duracion : datos.segundosEscuchados();
        if (segundos < 0) {
            throw ApiException.badRequest("Los segundos escuchados no pueden ser negativos");
        }
        LocalDateTime fecha = datos.fechaHora() == null ? LocalDateTime.now() : datos.fechaHora();

        int id = escuchaRepository.crear(idUsuario, datos.idCancion(), datos.idAlbum(),
                datos.idPlataforma(), segundos, fecha);
        return escuchaRepository.listarPorUsuario(idUsuario, 500).stream()
                .filter(e -> e.idEscucha() == id)
                .findFirst()
                .orElseThrow();
    }

    public void eliminar(int idUsuario, int idEscucha) {
        usuarioService.exigir(idUsuario);
        if (escuchaRepository.eliminar(idUsuario, idEscucha) == 0) {
            throw ApiException.notFound("No existe la escucha " + idEscucha + " para ese usuario");
        }
    }
}
