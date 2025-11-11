package com.gpp.servisoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gpp.servisoft.model.entities.Factura;

@Repository
public interface FacturacionRepository extends JpaRepository<Factura, Integer> {

}
