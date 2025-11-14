package com.gpp.servisoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gpp.servisoft.model.entities.Cliente;

/**
 * *Cliente Repository*
 * Interfaz del repositorio para manejar las operaciones de base de datos relacionadas con la entidad Cliente.
 */
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

}
