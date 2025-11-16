package com.gpp.servisoft.repository;

// --- 1. ¡IMPORTACIONES CORREGIDAS! ---
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gpp.servisoft.model.entities.Factura;

@Repository
public interface FacturacionRepository extends JpaRepository<Factura, Integer> {

    /**
     * Busca facturas usando filtros condicionales y paginación.
     */
    @Query(value = "SELECT f FROM Factura f " + // Selecciona todas las Facturas(Objeto Java)
        // Donde (Si el IdCuenta pasado como parametro":parametro", es nulo -> Devolve
        // todas las Facturas)
        // O si el id pasado como parametro tiene un valor devolve aquellas facturas las
        // cuales coincidan con el parametro
            "WHERE (:idCuenta IS NULL OR f.datosClienteFactura.idCuenta = :idCuenta) " +
            // Y, Si el comprobante pasado como parametro (0001-00000001) es nulo o vacio,
            // devuelvo todas las facturas que coincidan
            // Pero si tiene texto, deveulvo aquellas las cuales coincidan total o
            // parcialmente con el comporbante ingresado
            "AND (:comprobante IS NULL OR :comprobante = '' OR f.nroComprobante LIKE CONCAT('%', :comprobante, '%'))",
            // Consulta definida que me devuelve la cantidad de registros que satisface la
            // consulta value
            countQuery = "SELECT count(f) FROM Factura f " +
            // tienen que tener las mismas condiciones para que funcione correctamente
                    "WHERE (:idCuenta IS NULL OR f.datosClienteFactura.idCuenta = :idCuenta) " +
                    "AND (:comprobante IS NULL OR :comprobante = '' OR f.nroComprobante LIKE CONCAT('%', :comprobante, '%'))")
    Page<Factura> findFacturasByFilters(
            @Param("idCuenta") Integer idCuenta, 
            @Param("comprobante") String comprobante,
            Pageable pageable // <-- El "objeto-pregunta" de Spring
    );

    /**
     * Busca facturas pendientes (sin pagos) usando filtros condicionales y paginación.
     * Una factura se considera pendiente si aún no tiene pagos registrados o el total pagado es menor al monto total.
     */
    @Query(value = "SELECT f FROM Factura f " +
            "WHERE (:idCuenta IS NULL OR f.datosClienteFactura.idCuenta = :idCuenta) " +
            "AND (:comprobante IS NULL OR :comprobante = '' OR f.nroComprobante LIKE CONCAT('%', :comprobante, '%')) " +
            "AND f.idFactura NOT IN (SELECT DISTINCT p.factura.idFactura FROM Pago p)",
            countQuery = "SELECT count(f) FROM Factura f " +
                    "WHERE (:idCuenta IS NULL OR f.datosClienteFactura.idCuenta = :idCuenta) " +
                    "AND (:comprobante IS NULL OR :comprobante = '' OR f.nroComprobante LIKE CONCAT('%', :comprobante, '%')) " +
                    "AND f.idFactura NOT IN (SELECT DISTINCT p.factura.idFactura FROM Pago p)")
    Page<Factura> findFacturasPendientesByFilters(
            @Param("idCuenta") Integer idCuenta,
            @Param("comprobante") String comprobante,
            Pageable pageable
    );
}