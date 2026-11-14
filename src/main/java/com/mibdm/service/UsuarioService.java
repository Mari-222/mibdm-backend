package com.mibdm.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mibdm.exception.ApiException;
import com.mibdm.model.ArtistaDatos;
import com.mibdm.model.LoginRequest;
import com.mibdm.model.Usuario;
import com.mibdm.model.UsuarioActualizar;
import com.mibdm.model.UsuarioCrear;
import com.mibdm.repository.ArtistaRepository;
import com.mibdm.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ArtistaRepository artistaRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, ArtistaRepository artistaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.artistaRepository = artistaRepository;
    }

    /** Devuelve el usuario o lanza 404. Lo usan los demas servicios para validar el {idUsuario} de la ruta. */
    public Usuario exigir(int idUsuario) {
        return usuarioRepository.buscarPorId(idUsuario)
                .orElseThrow(() -> ApiException.notFound("No existe el usuario " + idUsuario));
    }

    public Usuario obtener(int idUsuario) {
        return exigir(idUsuario);
    }

    /** Inicio de sesion simplificado: se identifica al usuario por su correo (sin contrasena). */
    public Usuario login(LoginRequest request) {
        if (request == null || esVacio(request.correo())) {
            throw ApiException.badRequest("El correo es obligatorio");
        }
        return usuarioRepository.buscarPorCorreo(request.correo().trim())
                .orElseThrow(() -> ApiException.notFound("No existe un usuario con ese correo"));
    }

    /**
     * Registra una cuenta normal o de artista. Para un artista se crea primero su perfil de artista
     * y luego el usuario enlazado a el, todo en una transaccion.
     */
    @Transactional
    public Usuario registrar(UsuarioCrear datos) {
        if (datos == null || esVacio(datos.nombre()) || esVacio(datos.apellido()) || esVacio(datos.correo())) {
            throw ApiException.badRequest("Nombre, apellido y correo son obligatorios");
        }
        if (!datos.correo().contains("@")) {
            throw ApiException.badRequest("El correo no es valido");
        }
        String tipo = esVacio(datos.tipo()) ? "normal" : datos.tipo().trim().toLowerCase();
        if (!tipo.equals("normal") && !tipo.equals("artista")) {
            throw ApiException.badRequest("El tipo de cuenta debe ser normal o artista");
        }
        UsuarioCrear limpio = new UsuarioCrear(
                datos.nombre().trim(), datos.apellido().trim(), datos.correo().trim(),
                esVacio(datos.pais()) ? null : datos.pais().trim(), tipo, null);
        if (usuarioRepository.buscarPorCorreo(limpio.correo()).isPresent()) {
            throw ApiException.conflict("Ya existe un usuario con ese correo");
        }
        Integer idArtista = null;
        if (tipo.equals("artista")) {
            idArtista = artistaRepository.crear(limpiarArtista(datos.artista(), null));
        }
        int id = usuarioRepository.crear(limpio, idArtista);
        return exigir(id);
    }

    /**
     * Modifica el perfil. El tipo de cuenta no cambia. Si la cuenta es de artista y viene el objeto
     * artista, tambien se actualiza su perfil de artista.
     */
    @Transactional
    public Usuario actualizar(int idUsuario, UsuarioActualizar datos) {
        Usuario actual = exigir(idUsuario);
        if (datos == null || esVacio(datos.nombre()) || esVacio(datos.apellido()) || esVacio(datos.correo())) {
            throw ApiException.badRequest("Nombre, apellido y correo son obligatorios");
        }
        if (!datos.correo().contains("@")) {
            throw ApiException.badRequest("El correo no es valido");
        }
        String correo = datos.correo().trim();
        if (usuarioRepository.buscarPorCorreo(correo).filter(o -> o.idUsuario() != idUsuario).isPresent()) {
            throw ApiException.conflict("Ya existe un usuario con ese correo");
        }
        ArtistaDatos artista = null;
        if ("artista".equals(actual.tipo()) && actual.idArtista() != null && datos.artista() != null) {
            artista = limpiarArtista(datos.artista(), actual.idArtista());
        }
        usuarioRepository.actualizar(idUsuario, new UsuarioActualizar(
                datos.nombre().trim(), datos.apellido().trim(), correo,
                esVacio(datos.pais()) ? null : datos.pais().trim(), null));
        if (artista != null) {
            artistaRepository.actualizar(actual.idArtista(), artista);
        }
        return exigir(idUsuario);
    }

    /** Valida y normaliza los datos del perfil de artista. excluirId es el artista propio al editar (null al crear). */
    private ArtistaDatos limpiarArtista(ArtistaDatos a, Integer excluirId) {
        if (a == null || esVacio(a.nombre())) {
            throw ApiException.badRequest("El nombre artistico es obligatorio");
        }
        String tipo = esVacio(a.tipo()) ? "" : a.tipo().trim().toLowerCase();
        if (!tipo.equals("solista") && !tipo.equals("banda")) {
            throw ApiException.badRequest("El tipo de artista debe ser solista o banda");
        }
        String nombre = a.nombre().trim();
        if (artistaRepository.existePorNombre(nombre, excluirId)) {
            throw ApiException.conflict("Ya existe un artista con ese nombre");
        }
        return new ArtistaDatos(nombre, esVacio(a.paisOrigen()) ? null : a.paisOrigen().trim(), tipo);
    }

    private static boolean esVacio(String s) {
        return s == null || s.isBlank();
    }
}
