package com.gpp.servisoft.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gpp.servisoft.model.entities.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    @EntityGraph(attributePaths = { "factura" })
    @Query(value = "SELECT p FROM Pago p JOIN p.factura f "
	    + "WHERE (:cuentaId IS NULL OR f.datosClienteFactura.idCuenta = :cuentaId) "
	    + "AND (:comprobante IS NULL OR :comprobante = '' OR LOWER(f.nroComprobante) LIKE LOWER(CONCAT('%', :comprobante, '%'))) "
	    + "AND (:fechaDesde IS NULL OR p.fechaPago >= :fechaDesde) "
	    + "AND (:fechaHasta IS NULL OR p.fechaPago <= :fechaHasta)",
	    countQuery = "SELECT COUNT(p) FROM Pago p JOIN p.factura f "
		    + "WHERE (:cuentaId IS NULL OR f.datosClienteFactura.idCuenta = :cuentaId) "
		    + "AND (:comprobante IS NULL OR :comprobante = '' OR LOWER(f.nroComprobante) LIKE LOWER(CONCAT('%', :comprobante, '%'))) "
		    + "AND (:fechaDesde IS NULL OR p.fechaPago >= :fechaDesde) "
		    + "AND (:fechaHasta IS NULL OR p.fechaPago <= :fechaHasta)")
    Page<Pago> findByFilters(@Param("cuentaId") Integer cuentaId,
	    @Param("comprobante") String comprobante,
	    @Param("fechaDesde") LocalDate fechaDesde,
	    @Param("fechaHasta") LocalDate fechaHasta,
	    Pageable pageable);
}
