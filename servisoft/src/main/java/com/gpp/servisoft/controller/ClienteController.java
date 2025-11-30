package com.gpp.servisoft.controller;

import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.gpp.servisoft.model.entities.Cliente;
import com.gpp.servisoft.model.enums.Estado;
import com.gpp.servisoft.model.enums.TipoCliente;
import com.gpp.servisoft.service.ClienteService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    
    /**
     * Listar todos los clientes
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        return "clientes/listarCliente";
    }
    
    /**
     * Mostrar formulario para crear nuevo cliente
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Cliente cliente = new Cliente();
        cliente.setEstado(Estado.ACTIVO);
        model.addAttribute("cliente", cliente);
        model.addAttribute("tiposCliente", TipoCliente.values());
        // Solo permitir seleccionar ACTIVO e INACTIVO en el formulario
        model.addAttribute("estados", new Estado[] { Estado.ACTIVO, Estado.INACTIVO });
        return "clientes/formularioCliente";
    }
    
    /**
     * Mostrar formulario para editar cliente existente
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cliente> clienteOpt = clienteService.obtenerPorId(id);
        
        if (clienteOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El cliente no existe");
            return "redirect:/clientes";
        }
        
        model.addAttribute("cliente", clienteOpt.get());
        model.addAttribute("tiposCliente", TipoCliente.values());
        // Solo permitir seleccionar ACTIVO e INACTIVO en el formulario
        model.addAttribute("estados", new Estado[] { Estado.ACTIVO, Estado.INACTIVO });
        return "clientes/formularioCliente";
    }
    
    /**
     * Guardar o actualizar un cliente
     */
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Cliente cliente, 
                         BindingResult result, 
                         Model model, 
                         RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("tiposCliente", TipoCliente.values());
            // Solo permitir seleccionar ACTIVO e INACTIVO cuando hay errores y se re-renderiza
            model.addAttribute("estados", new Estado[] { Estado.ACTIVO, Estado.INACTIVO });
            return "clientes/formularioCliente";
        }
        
        try {
            if (cliente.getIdCliente() == 0) {
                // Es un nuevo cliente
                clienteService.guardar(cliente);
                redirectAttributes.addFlashAttribute("success", "Cliente creado exitosamente");
            } else {
                // Es una actualización
                clienteService.actualizar(cliente);
                redirectAttributes.addFlashAttribute("success", "Cliente actualizado exitosamente");
            }
            return "redirect:/clientes";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("tiposCliente", TipoCliente.values());
            model.addAttribute("estados", new Estado[] { Estado.ACTIVO, Estado.INACTIVO });
            return "clientes/formularioCliente";
        }
    }
    
    /**
     * Eliminar un cliente
     */
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Cliente suspendido exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "No se pudo suspender el cliente.");
        }
        return "redirect:/clientes";
    }
    
    /**
     * Cambiar el estado de un cliente
     */
    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable int id, 
                               @RequestParam Estado estado, 
                               RedirectAttributes redirectAttributes) {
        try {
            clienteService.cambiarEstado(id, estado);
            redirectAttributes.addFlashAttribute("success", "Estado actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo cambiar el estado del cliente");
        }
        return "redirect:/clientes";
    }
}