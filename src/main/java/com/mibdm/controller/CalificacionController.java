package com.mibdm.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mibdm.model.CalificacionCrear;
import com.mibdm.model.CalificacionVista;
import com.mibdm.service.CalificacionService;

@RestController
@RequestMapping("/api/usuarios/{idUsuario}/calificaciones")
public class CalificacionController {

    private final CalificacionService calificacionService;

    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    // ----- canciones -----

    @GetMapping("/canciones")
    public List<CalificacionVista> listarCanciones(@PathVariable int idUsuario) {
        return calificacionService.listarCanciones(idUsuario);
    }

    @PutMapping("/canciones/{idCancion}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void calificarCancion(@PathVariable int idUsuario, @PathVariable int idCancion,
                                 @RequestBody CalificacionCrear datos) {
        calificacionService.calificarCancion(idUsuario, idCancion, datos);
    }

    @DeleteMapping("/canciones/{idCancion}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarCancion(@PathVariable int idUsuario, @PathVariable int idCancion) {
        calificacionService.eliminarCancion(idUsuario, idCancion);
    }

    // ----- albumes -----

    @GetMapping("/albumes")
    public List<CalificacionVista> listarAlbumes(@PathVariable int idUsuario) {
        return calificacionService.listarAlbumes(idUsuario);
    }

    @PutMapping("/albumes/{idAlbum}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void calificarAlbum(@PathVariable int idUsuario, @PathVariable int idAlbum,
                               @RequestBody CalificacionCrear datos) {
        calificacionService.calificarAlbum(idUsuario, idAlbum, datos);
    }

    @DeleteMapping("/albumes/{idAlbum}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarAlbum(@PathVariable int idUsuario, @PathVariable int idAlbum) {
        calificacionService.eliminarAlbum(idUsuario, idAlbum);
    }
}
