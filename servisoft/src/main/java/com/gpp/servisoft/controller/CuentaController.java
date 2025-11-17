package com.gpp.servisoft.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gpp.servisoft.model.entities.Cuenta;
import com.gpp.servisoft.model.entities.ServicioDeLaCuenta;
import com.gpp.servisoft.model.dto.CuentaResponseDto;
import com.gpp.servisoft.service.ClienteService;
import com.gpp.servisoft.service.CuentaService;
import com.gpp.servisoft.service.ServicioDeLaCuentaService;
import com.gpp.servisoft.service.ServicioService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

/**
 * *Cuenta Controller*
 * Métodos del controlador para manejar las solicitudes HTTP relacionadas con la entidad Cuenta.
 */
@Controller
@RequestMapping("/cuentas")
public class CuentaController {
    
    @Autowired
    private CuentaService cuentaService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private ServicioService servicioService;
    
    @Autowired
    private ServicioDeLaCuentaService servicioDeLaCuentaService;

    /**
     * Muestra el listado de todas las cuentas no eliminadas.
     */
    @GetMapping
    public String listarCuentas(Model model) {
        model.addAttribute("cuentas", cuentaService.obtenerCuentasActivas());
        return "cuentas/lista";
    }

    /**
     * Muestra el formulario para crear una nueva cuenta
     */
    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("servicios", servicioService.listarActivos());
        return "cuentas/formulario";
    }

    /**
     * Crear una nueva cuenta asociada a un cliente específico.
     * @param cuenta
     * @param idCliente
     * @return Si los campos son válidos, se da el alta de una cuenta con la identificación del cliente asociado y se retorna.
     */
    @PostMapping("/cliente/{idCliente}")
    @ResponseBody
    public ResponseEntity<CuentaResponseDto> crearCuenta(
            @Valid @RequestBody Cuenta cuenta,
            @PathVariable int idCliente) {

        Cuenta cuentaGuardada = cuentaService.altaCuenta(cuenta, idCliente);
        CuentaResponseDto response = new CuentaResponseDto(
            cuentaGuardada.getIdCuenta(),
            cuentaGuardada.getRazonSocial(),
            cuentaGuardada.getCuit(),
            cuentaGuardada.getCondicionFrenteIVA(),
            cuentaGuardada.getDomicilioFiscal(),
            cuentaGuardada.getEstado()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener una cuenta por su ID.
     * @param id
     * @return La cuenta correspondiente al ID proporcionado.
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Cuenta> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(cuentaService.obtenerPorId(id));
    }

    /**
     * Actualizar una cuenta existente.
     * @param id ID de la cuenta a actualizar
     * @param cuenta Datos actualizados de la cuenta
     * @return La cuenta actualizada
     */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<CuentaResponseDto> actualizarCuenta(
            @PathVariable int id,
            @Valid @RequestBody Cuenta cuenta) {
        Cuenta cuentaActualizada = cuentaService.modificarCuenta(id, cuenta);
        CuentaResponseDto response = new CuentaResponseDto(
            cuentaActualizada.getIdCuenta(),
            cuentaActualizada.getRazonSocial(),
            cuentaActualizada.getCuit(),
            cuentaActualizada.getCondicionFrenteIVA(),
            cuentaActualizada.getDomicilioFiscal(),
            cuentaActualizada.getEstado()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Muestra el formulario para actualizar los datos de una cuenta existente.
     * @param id dato identificador de la cuenta a actualizar.
     * @param cuenta datos actualizados de la cuenta.
     * @return La cuenta actualizada con los nuevos datos proporcionados.
     */
    @GetMapping("/editar/{id}")
    @Transactional
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        model.addAttribute("cuenta", cuentaService.obtenerPorId(id));
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("servicios", servicioService.listarActivos());
        return "cuentas/formulario";
    }

    /**
     * Realiza soft delete de una cuenta específica identificada por su ID.
     * Cambia el estado de una cuenta a INACTIVO.
     * @param id dato identificador de la cuenta.
     * @return Respuesta sin contenido si la eliminación fue exitosa.
     */
    @PostMapping("/baja/{id}")
    public String bajaCuenta(@PathVariable int id) {
        cuentaService.bajaCuenta(id);
        return "redirect:/cuentas";
    }

    /**
     * Muestra los detalles de una cuenta específica
     */
    @GetMapping("/detalle/{id}")
    @Transactional
    public String mostrarDetalle(@PathVariable int id, Model model) {
        model.addAttribute("cuenta", cuentaService.obtenerPorId(id));
        return "cuentas/detalle";
    }

    /**
     * Agrega un servicio a una cuenta existente
     */
    @PostMapping("/{idCuenta}/servicios/{idServicio}")
    @ResponseBody
    public ResponseEntity<ServicioDeLaCuenta> agregarServicio(
            @PathVariable int idCuenta,
            @PathVariable int idServicio) {
        try {
            ServicioDeLaCuenta servicioCuenta = servicioDeLaCuentaService.agregarServicioACuenta(idCuenta, idServicio);
            return ResponseEntity.ok(servicioCuenta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Agrega múltiples servicios a una cuenta (para formulario de creación/edición)
     */
    @PostMapping("/{idCuenta}/servicios/multiple")
    @ResponseBody
    public ResponseEntity<?> agregarMultiplesServicios(
            @PathVariable int idCuenta,
            @RequestParam List<Integer> servicios) {
        try {
            servicioDeLaCuentaService.agregarMultiplesServicios(idCuenta, servicios);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Elimina un servicio de una cuenta
     */
    @DeleteMapping("/servicios/{idServicioCuenta}")
    @ResponseBody
    public ResponseEntity<?> eliminarServicio(@PathVariable int idServicioCuenta) {
        try {
            servicioDeLaCuentaService.eliminarServicioDeCuenta(idServicioCuenta);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtiene los servicios de una cuenta específica
     */
    @GetMapping("/{idCuenta}/servicios")
    @ResponseBody
    public ResponseEntity<List<ServicioDeLaCuenta>> obtenerServiciosDeCuenta(@PathVariable int idCuenta) {
        return ResponseEntity.ok(servicioDeLaCuentaService.obtenerServiciosDeCuenta(idCuenta));
    }
}