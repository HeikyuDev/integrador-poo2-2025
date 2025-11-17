package com.gpp.servisoft.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gpp.servisoft.model.entities.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer>{

}
