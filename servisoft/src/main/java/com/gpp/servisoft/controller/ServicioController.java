package com.gpp.servisoft.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.gpp.servisoft.service.ServicioService;
import com.gpp.servisoft.model.entities.Servicio;
import com.gpp.servisoft.model.enums.Estado;

@Controller
@RequestMapping("/servicios")
public class ServicioController {

     @Autowired
    private ServicioService servicioService;

    /**
     * Muestra el dashboard con la lista de todos los servicios
     */
    @GetMapping
    public String listarServicios(@RequestParam(name = "estado", required = false) String estado, Model model) {

        // Por defecto mostrar solo ACTIVO; si se pasa estado, filtrar por ese
        List<Servicio> servicios;
        if (estado == null || estado.isBlank()) {
            servicios = servicioService.listarActivos();
        } else if ("ALL".equalsIgnoreCase(estado)) {
            servicios = servicioService.listarTodos();
        } else {
            Estado estadoEnum = Estado.valueOf(estado);
            servicios = servicioService.listarPorEstado(estadoEnum);
        }

        // Calcular estadísticas
        long totalServicios = servicios.size();
        long serviciosActivos = servicios.stream()
                .filter(s -> s.getEstado() == Estado.ACTIVO)
                .count();
        long serviciosInactivos = servicios.stream()
                .filter(s -> s.getEstado() == Estado.INACTIVO)
                .count();
        double montoPromedio = servicios.stream()
                .mapToDouble(Servicio::getMontoServicio)
                .average()
                .orElse(0.0);

        model.addAttribute("servicios", servicios);
        model.addAttribute("estadoSeleccionado", estado == null || estado.isBlank() ? "ACTIVO" : estado);
        model.addAttribute("totalServicios", totalServicios);
        model.addAttribute("serviciosActivos", serviciosActivos);
        model.addAttribute("serviciosInactivos", serviciosInactivos);
        model.addAttribute("montoPromedio", montoPromedio);

        return "servicios/dashboardServicio";
    }

    /**
     * Muestra el formulario para crear un nuevo servicio
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Servicio servicio = new Servicio();
        servicio.setEstado(Estado.ACTIVO); // Estado por defecto
        model.addAttribute("servicio", servicio);
        return "servicios/formularioServicio";
    }

    /**
     * Muestra el formulario para editar un servicio existente
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") int id, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Servicio servicio = servicioService.buscarPorId(id);
            if (servicio == null) {
                redirectAttributes.addFlashAttribute("error", "El servicio no existe");
                return "redirect:/servicios";
            }
            model.addAttribute("servicio", servicio);
            return "servicios/formularioServicio";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el servicio: " + e.getMessage());
            return "redirect:/servicios";
        }
    }

    /**
     * Guarda un nuevo servicio en la base de datos
     */
    @PostMapping("/guardar")
    public String guardarServicio(@Valid @ModelAttribute("servicio") Servicio servicio,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Si hay errores de validación, volver al formulario
        if (result.hasErrors()) {
            model.addAttribute("servicio", servicio);
            return "servicios/formularioServicio";
        }

        try {
            servicioService.guardar(servicio);
            redirectAttributes.addFlashAttribute("mensaje",
                    "Servicio '" + servicio.getNombreServicio() + "' creado exitosamente");
            return "redirect:/servicios";
        } catch (DataIntegrityViolationException e) {
            // Error por constraint única (carrera o validación de BD)
            result.rejectValue("nombreServicio", "duplicado", "Ya existe un servicio con ese nombre");
            model.addAttribute("servicio", servicio);
            return "servicios/formularioServicio";
        } catch (IllegalArgumentException e) {
            // Validaciones de negocio del service (nombre duplicado u otra regla)
            result.rejectValue("nombreServicio", "duplicado", "Ya existe un servicio con ese nombre");
            model.addAttribute("servicio", servicio);
            return "servicios/formularioServicio";
        } catch (Exception e) {
            model.addAttribute("servicio", servicio);
            model.addAttribute("error", "Error al guardar el servicio: " + e.getMessage());
            return "servicios/formularioServicio";
        }
    }

    /**
     * Actualiza un servicio existente
     */
    @PostMapping("/actualizar")
    public String actualizarServicio(@Valid @ModelAttribute("servicio") Servicio servicio,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            System.out.println("⚠️ Errores detectados, recargando formulario...");
            model.addAttribute("servicio", servicio);
            return "servicios/formularioServicio";
        }

        try {
            // 🔍 Log temporal para debug
            System.out.println("Actualizando servicio ID: " + servicio.getIdServicio());

            servicioService.actualizar(servicio);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Servicio '" + servicio.getNombreServicio() + "' actualizado exitosamente");

            return "redirect:/servicios";
        } catch (DataIntegrityViolationException e) {
            result.rejectValue("nombreServicio", "duplicado", "Ya existe un servicio con ese nombre");
            model.addAttribute("servicio", servicio);
            return "servicios/formularioServicio";
        } catch (IllegalArgumentException e) {
            result.rejectValue("nombreServicio", "duplicado", "Ya existe un servicio con ese nombre");
            model.addAttribute("servicio", servicio);
            return "servicios/formularioServicio";
        } catch (Exception e) {
            model.addAttribute("servicio", servicio);
            model.addAttribute("error", "Error al actualizar el servicio: " + e.getMessage());
            return "servicios/formularioServicio";
        }
    }

    /**
     * Elimina un servicio (borrado lógico - marca como INACTIVO)
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarServicio(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            // Obtener el nombre del servicio antes de eliminarlo para el mensaje
            Servicio servicio = servicioService.buscarPorId(id);
            if (servicio == null) {
                redirectAttributes.addFlashAttribute("error", "El servicio no existe");
                return "redirect:/servicios";
            }
            
            String nombreServicio = servicio.getNombreServicio();
            servicioService.eliminar(id);
            
            redirectAttributes.addFlashAttribute("mensaje",
                    "Servicio '" + nombreServicio + "' marcado como INACTIVO exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "No se pudo desactivar el servicio: " + e.getMessage());
        }

        return "redirect:/servicios";
    }

    /**
     * Cambia el estado de un servicio (Activo/Inactivo)
     */
    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            Servicio servicio = servicioService.buscarPorId(id);
            if (servicio == null) {
                redirectAttributes.addFlashAttribute("error", "El servicio no existe");
                return "redirect:/servicios";
            }

            // Cambiar el estado usando el método del service
            Estado nuevoEstado = servicio.getEstado() == Estado.ACTIVO ? Estado.INACTIVO : Estado.ACTIVO;
            servicioService.cambiarEstado(id, nuevoEstado);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Estado del servicio actualizado a " + nuevoEstado);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al cambiar el estado: " + e.getMessage());
        }

        return "redirect:/servicios";
    }
}
