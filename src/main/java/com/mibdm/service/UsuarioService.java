package com.mibdm.service;

import org.springframework.stereotype.Service;

import com.mibdm.exception.ApiException;
import com.mibdm.model.LoginRequest;
import com.mibdm.model.Usuario;
import com.mibdm.model.UsuarioCrear;
import com.mibdm.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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

    public Usuario registrar(UsuarioCrear datos) {
        if (datos == null || esVacio(datos.nombre()) || esVacio(datos.apellido()) || esVacio(datos.correo())) {
            throw ApiException.badRequest("Nombre, apellido y correo son obligatorios");
        }
        if (!datos.correo().contains("@")) {
            throw ApiException.badRequest("El correo no es valido");
        }
        UsuarioCrear limpio = new UsuarioCrear(
                datos.nombre().trim(), datos.apellido().trim(), datos.correo().trim(),
                esVacio(datos.pais()) ? null : datos.pais().trim());
        if (usuarioRepository.buscarPorCorreo(limpio.correo()).isPresent()) {
            throw ApiException.conflict("Ya existe un usuario con ese correo");
        }
        int id = usuarioRepository.crear(limpio);
        return exigir(id);
    }

    private static boolean esVacio(String s) {
        return s == null || s.isBlank();
    }
}
