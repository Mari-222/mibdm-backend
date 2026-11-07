package com.mibdm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mibdm.model.LoginRequest;
import com.mibdm.model.Usuario;
import com.mibdm.model.UsuarioCrear;
import com.mibdm.service.UsuarioService;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/auth/login")
    public Usuario login(@RequestBody LoginRequest request) {
        return usuarioService.login(request);
    }

    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario registrar(@RequestBody UsuarioCrear datos) {
        return usuarioService.registrar(datos);
    }

    @GetMapping("/usuarios/{idUsuario}")
    public Usuario obtener(@PathVariable int idUsuario) {
        return usuarioService.obtener(idUsuario);
    }
}
