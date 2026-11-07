package com.mibdm.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mibdm.exception.ApiException;
import com.mibdm.model.Album;
import com.mibdm.model.AlbumCrear;
import com.mibdm.model.AlbumDetalle;
import com.mibdm.model.Artista;
import com.mibdm.model.ArtistaDetalle;
import com.mibdm.model.Cancion;
import com.mibdm.model.CancionCrear;
import com.mibdm.model.CancionDetalle;
import com.mibdm.model.Genero;
import com.mibdm.model.Plataforma;
import com.mibdm.model.Usuario;
import com.mibdm.repository.AlbumRepository;
import com.mibdm.repository.ArtistaRepository;
import com.mibdm.repository.CancionRepository;
import com.mibdm.repository.GeneroRepository;
import com.mibdm.repository.PlataformaRepository;

/** Consulta del catalogo (artistas, albumes, canciones, generos, plataformas) y alta de catalogo por artistas. */
@Service
public class CatalogoService {

    private static final Set<String> TIPOS_ALBUM = Set.of("LP", "EP", "sencillo", "recopilatorio");

    private final ArtistaRepository artistaRepository;
    private final AlbumRepository albumRepository;
    private final CancionRepository cancionRepository;
    private final GeneroRepository generoRepository;
    private final PlataformaRepository plataformaRepository;
    private final UsuarioService usuarioService;

    public CatalogoService(ArtistaRepository artistaRepository,
                           AlbumRepository albumRepository,
                           CancionRepository cancionRepository,
                           GeneroRepository generoRepository,
                           PlataformaRepository plataformaRepository,
                           UsuarioService usuarioService) {
        this.artistaRepository = artistaRepository;
        this.albumRepository = albumRepository;
        this.cancionRepository = cancionRepository;
        this.generoRepository = generoRepository;
        this.plataformaRepository = plataformaRepository;
        this.usuarioService = usuarioService;
    }

    // ---------- consultas ----------

    public List<Artista> listarArtistas(String q) {
        return artistaRepository.listar(q);
    }

    public ArtistaDetalle detalleArtista(int idArtista) {
        Artista artista = artistaRepository.buscarPorId(idArtista)
                .orElseThrow(() -> ApiException.notFound("No existe el artista " + idArtista));
        return new ArtistaDetalle(artista, albumRepository.listarPorArtista(idArtista));
    }

    public List<Album> listarAlbumes(String q) {
        return albumRepository.listar(q);
    }

    public AlbumDetalle detalleAlbum(int idAlbum) {
        Album album = albumRepository.buscarPorId(idAlbum)
                .orElseThrow(() -> ApiException.notFound("No existe el album " + idAlbum));
        return new AlbumDetalle(album,
                cancionRepository.listarPorAlbum(idAlbum),
                albumRepository.promedioCalificaciones(idAlbum),
                albumRepository.totalCalificaciones(idAlbum));
    }

    public List<Cancion> listarCanciones(String q, Integer idGenero) {
        return cancionRepository.listar(q, idGenero);
    }

    public CancionDetalle detalleCancion(int idCancion) {
        Cancion cancion = cancionRepository.buscarPorId(idCancion)
                .orElseThrow(() -> ApiException.notFound("No existe la cancion " + idCancion));
        return new CancionDetalle(cancion,
                cancionRepository.participantes(idCancion),
                cancionRepository.generos(idCancion),
                cancionRepository.promedioCalificaciones(idCancion),
                cancionRepository.totalCalificaciones(idCancion));
    }

    public List<Genero> listarGeneros() {
        return generoRepository.listar();
    }

    public List<Plataforma> listarPlataformas() {
        return plataformaRepository.listar();
    }

    // ---------- alta de catalogo (solo usuarios tipo 'artista') ----------

    public Album crearAlbum(AlbumCrear datos) {
        if (datos == null || datos.titulo() == null || datos.titulo().isBlank()) {
            throw ApiException.badRequest("El titulo es obligatorio");
        }
        exigirArtista(datos.idUsuario());
        String tipo = datos.tipo() == null || datos.tipo().isBlank() ? "LP" : datos.tipo();
        if (!TIPOS_ALBUM.contains(tipo)) {
            throw ApiException.badRequest("Tipo de album invalido. Use: LP, EP, sencillo o recopilatorio");
        }
        if (datos.anioLanzamiento() < 1900 || datos.anioLanzamiento() > 2100) {
            throw ApiException.badRequest("El anio de lanzamiento debe estar entre 1900 y 2100");
        }
        if (!artistaRepository.existe(datos.idArtista())) {
            throw ApiException.notFound("No existe el artista " + datos.idArtista());
        }
        int id = albumRepository.crear(new AlbumCrear(
                datos.idUsuario(), datos.titulo().trim(), datos.anioLanzamiento(), tipo, datos.idArtista()));
        return albumRepository.buscarPorId(id).orElseThrow();
    }

    /** Inserta la cancion, sus interpretes y sus generos en una sola transaccion. */
    @Transactional
    public CancionDetalle crearCancion(CancionCrear datos) {
        if (datos == null || datos.titulo() == null || datos.titulo().isBlank()) {
            throw ApiException.badRequest("El titulo es obligatorio");
        }
        exigirArtista(datos.idUsuario());
        if (datos.duracionSeg() <= 0) {
            throw ApiException.badRequest("La duracion debe ser mayor que 0");
        }
        if (datos.numeroPista() != null && datos.numeroPista() <= 0) {
            throw ApiException.badRequest("El numero de pista debe ser mayor que 0");
        }
        if (datos.numeroPista() != null && datos.idAlbum() == null) {
            throw ApiException.badRequest("Una cancion sin album no puede tener numero de pista");
        }
        if (datos.idAlbum() != null && !albumRepository.existe(datos.idAlbum())) {
            throw ApiException.notFound("No existe el album " + datos.idAlbum());
        }
        if (datos.idsInterpretes() == null || datos.idsInterpretes().isEmpty()) {
            throw ApiException.badRequest("Debe indicar al menos un interprete");
        }
        for (int idArtista : datos.idsInterpretes()) {
            if (!artistaRepository.existe(idArtista)) {
                throw ApiException.notFound("No existe el artista " + idArtista);
            }
        }
        List<Integer> generos = datos.idsGeneros() == null ? List.of() : datos.idsGeneros();
        for (int idGenero : generos) {
            if (!generoRepository.existe(idGenero)) {
                throw ApiException.notFound("No existe el genero " + idGenero);
            }
        }

        int idCancion = cancionRepository.crear(
                datos.titulo().trim(), datos.duracionSeg(), datos.numeroPista(), datos.idAlbum());
        for (int idArtista : datos.idsInterpretes()) {
            cancionRepository.agregarArtista(idCancion, idArtista, "interprete");
        }
        for (int idGenero : generos) {
            cancionRepository.agregarGenero(idCancion, idGenero);
        }
        return detalleCancion(idCancion);
    }

    private void exigirArtista(int idUsuario) {
        Usuario u = usuarioService.exigir(idUsuario);
        if (!"artista".equals(u.tipo())) {
            throw ApiException.forbidden("Solo los usuarios de tipo artista pueden registrar catalogo");
        }
    }
}
