package com.mibdm.repository;

import java.util.Optional;

import com.mibdm.model.Usuario;
import com.mibdm.model.UsuarioActualizar;
import com.mibdm.model.UsuarioCrear;

public interface UsuarioRepository {

    Optional<Usuario> buscarPorId(int idUsuario);

    Optional<Usuario> buscarPorCorreo(String correo);

    /**
     * Inserta el usuario y devuelve el id generado. usuario.tipo() ya viene validado ("normal" o
     * "artista"); idArtista es null para un usuario normal y el perfil de artista para uno artista.
     */
    int crear(UsuarioCrear usuario, Integer idArtista);

    /** Actualiza nombre, apellido, correo y pais del usuario. */
    void actualizar(int idUsuario, UsuarioActualizar datos);
}
