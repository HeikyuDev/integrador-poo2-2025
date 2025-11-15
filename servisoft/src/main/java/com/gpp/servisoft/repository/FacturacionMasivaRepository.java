package com.gpp.servisoft.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gpp.servisoft.model.entities.FacturacionMasiva;

@Repository
public interface FacturacionMasivaRepository extends JpaRepository<FacturacionMasiva, Integer> {

    @Override
    Page<FacturacionMasiva> findAll(Pageable pageable);
}
