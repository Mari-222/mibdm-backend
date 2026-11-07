package com.mibdm.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mibdm.model.EscuchaCrear;
import com.mibdm.model.EscuchaVista;
import com.mibdm.service.EscuchaService;

@RestController
@RequestMapping("/api/usuarios/{idUsuario}/escuchas")
public class EscuchaController {

    private final EscuchaService escuchaService;

    public EscuchaController(EscuchaService escuchaService) {
        this.escuchaService = escuchaService;
    }

    @GetMapping
    public List<EscuchaVista> listar(@PathVariable int idUsuario,
                                     @RequestParam(defaultValue = "50") int limite) {
        return escuchaService.listar(idUsuario, limite);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EscuchaVista registrar(@PathVariable int idUsuario, @RequestBody EscuchaCrear datos) {
        return escuchaService.registrar(idUsuario, datos);
    }

    @DeleteMapping("/{idEscucha}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable int idUsuario, @PathVariable int idEscucha) {
        escuchaService.eliminar(idUsuario, idEscucha);
    }
}
