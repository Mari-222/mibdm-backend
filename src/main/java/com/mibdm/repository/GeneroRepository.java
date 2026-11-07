package com.mibdm.repository;

import java.util.List;

import com.mibdm.model.Genero;

public interface GeneroRepository {

    List<Genero> listar();

    boolean existe(int idGenero);
}
