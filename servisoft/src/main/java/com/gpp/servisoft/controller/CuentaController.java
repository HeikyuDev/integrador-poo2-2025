package com.gpp.servisoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gpp.servisoft.model.entities.Cuenta;
import com.gpp.servisoft.service.CuentaService;
import com.gpp.servisoft.service.ClienteService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;


/**
 * *Cuenta Controller*
 * Métodos del controlador para manejar las solicitudes HTTP relacionadas con la entidad Cuenta.
 */

@Controller
@RequestMapping("/cuentas")
public class CuentaController {
    /**
     * {@link CuentaService} ; {@link ClienteService}  Inyección de la dependencia del servicio CuentaService, para utilizar 
     * los métodos de negocio relacionados con la entidad Cuenta y Cliente.
     */

    @Autowired
    private CuentaService cuentaService;
    @Autowired
    private ClienteService clienteService;

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
    public ResponseEntity<Cuenta> crearCuenta(
            @Valid @RequestBody Cuenta cuenta,
            @PathVariable int idCliente) {

        return ResponseEntity.ok(cuentaService.altaCuenta(cuenta, idCliente));
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
    public ResponseEntity<Cuenta> actualizarCuenta(
            @PathVariable int id,
            @Valid @RequestBody Cuenta cuenta) {
        return ResponseEntity.ok(cuentaService.modificarCuenta(id, cuenta));
    }


/**
 * Muestra el formulario para actualizar los datos de una cuenta existente.
 * @param id dato identificador de la cuenta a actualizar.
 * @param cuenta datos actualizados de la cuenta.
 * @return La cuenta actualizada con los nuevos datos proporcionados.
 */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        model.addAttribute("cuenta", cuentaService.obtenerPorId(id));
        model.addAttribute("clientes", clienteService.obtenerTodos());
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
}