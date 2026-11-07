package com.mibdm.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
import com.mibdm.service.CatalogoService;

@RestController
@RequestMapping("/api")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/artistas")
    public List<Artista> artistas(@RequestParam(required = false) String q) {
        return catalogoService.listarArtistas(q);
    }

    @GetMapping("/artistas/{idArtista}")
    public ArtistaDetalle artista(@PathVariable int idArtista) {
        return catalogoService.detalleArtista(idArtista);
    }

    @GetMapping("/albumes")
    public List<Album> albumes(@RequestParam(required = false) String q) {
        return catalogoService.listarAlbumes(q);
    }

    @GetMapping("/albumes/{idAlbum}")
    public AlbumDetalle album(@PathVariable int idAlbum) {
        return catalogoService.detalleAlbum(idAlbum);
    }

    @PostMapping("/albumes")
    @ResponseStatus(HttpStatus.CREATED)
    public Album crearAlbum(@RequestBody AlbumCrear datos) {
        return catalogoService.crearAlbum(datos);
    }

    @GetMapping("/canciones")
    public List<Cancion> canciones(@RequestParam(required = false) String q,
                                   @RequestParam(required = false) Integer idGenero) {
        return catalogoService.listarCanciones(q, idGenero);
    }

    @GetMapping("/canciones/{idCancion}")
    public CancionDetalle cancion(@PathVariable int idCancion) {
        return catalogoService.detalleCancion(idCancion);
    }

    @PostMapping("/canciones")
    @ResponseStatus(HttpStatus.CREATED)
    public CancionDetalle crearCancion(@RequestBody CancionCrear datos) {
        return catalogoService.crearCancion(datos);
    }

    @GetMapping("/generos")
    public List<Genero> generos() {
        return catalogoService.listarGeneros();
    }

    @GetMapping("/plataformas")
    public List<Plataforma> plataformas() {
        return catalogoService.listarPlataformas();
    }
}
