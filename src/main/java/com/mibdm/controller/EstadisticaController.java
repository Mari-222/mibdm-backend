package com.mibdm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mibdm.model.Estadisticas;
import com.mibdm.service.EstadisticaService;

@RestController
@RequestMapping("/api/usuarios/{idUsuario}/estadisticas")
public class EstadisticaController {

    private final EstadisticaService estadisticaService;

    public EstadisticaController(EstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }

    @GetMapping
    public Estadisticas resumen(@PathVariable int idUsuario) {
        return estadisticaService.resumen(idUsuario);
    }
}
