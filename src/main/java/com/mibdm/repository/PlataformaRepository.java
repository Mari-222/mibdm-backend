package com.mibdm.repository;

import java.util.List;

import com.mibdm.model.Plataforma;

public interface PlataformaRepository {

    List<Plataforma> listar();

    boolean existe(int idPlataforma);
}
