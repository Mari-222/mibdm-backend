package com.mibdm.repository;

import java.util.Optional;

import com.mibdm.model.Usuario;
import com.mibdm.model.UsuarioCrear;

public interface UsuarioRepository {

    Optional<Usuario> buscarPorId(int idUsuario);

    Optional<Usuario> buscarPorCorreo(String correo);

    /** Inserta un usuario normal y devuelve el id generado. */
    int crear(UsuarioCrear usuario);
}
