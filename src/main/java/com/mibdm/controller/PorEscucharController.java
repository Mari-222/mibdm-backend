package com.mibdm.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mibdm.model.PorEscucharActualizar;
import com.mibdm.model.PorEscucharCrear;
import com.mibdm.model.PorEscucharVista;
import com.mibdm.service.PorEscucharService;

@RestController
@RequestMapping("/api/usuarios/{idUsuario}/por-escuchar")
public class PorEscucharController {

    private final PorEscucharService porEscucharService;

    public PorEscucharController(PorEscucharService porEscucharService) {
        this.porEscucharService = porEscucharService;
    }

    @GetMapping
    public List<PorEscucharVista> listar(@PathVariable int idUsuario) {
        return porEscucharService.listar(idUsuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void agregar(@PathVariable int idUsuario, @RequestBody PorEscucharCrear datos) {
        porEscucharService.agregar(idUsuario, datos);
    }

    @PutMapping("/{idAlbum}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void actualizar(@PathVariable int idUsuario, @PathVariable int idAlbum,
                           @RequestBody PorEscucharActualizar datos) {
        porEscucharService.actualizar(idUsuario, idAlbum, datos);
    }

    @DeleteMapping("/{idAlbum}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable int idUsuario, @PathVariable int idAlbum) {
        porEscucharService.eliminar(idUsuario, idAlbum);
    }
}
