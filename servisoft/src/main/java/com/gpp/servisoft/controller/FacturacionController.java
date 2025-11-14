package com.gpp.servisoft.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gpp.servisoft.model.dto.FacturacionFormDto;
import com.gpp.servisoft.model.dto.ServicioDeLaCuentaDto;
import com.gpp.servisoft.model.dto.ServicioSeleccionadoDto;
import com.gpp.servisoft.model.enums.Periodicidad;
import com.gpp.servisoft.services.CuentaServicio;
import com.gpp.servisoft.services.FacturacionService;
import com.gpp.servisoft.services.ServicioDeLaCuentaService;


/**
 * Controlador ligero para servir páginas Thymeleaf.
 *
 * Nota: la lógica dinámica (listas, acciones) debe realizarse mediante el
 * REST API (ej.: FacturacionController) y ser consumida desde las vistas
 * mediante fetch/AJAX. Mantener este controlador simple facilita separar UI y
 * API.
 */
@Controller
@RequestMapping("/facturacion")
public class FacturacionController {

    @Autowired
    private CuentaServicio cuentaServicio;

    @Autowired
    private ServicioDeLaCuentaService servicioDeLaCuentaServicio;

    @Autowired
    private FacturacionService facturacionService;

    // utiliza el id de la cuenta seleccionado en el Combo para mostrar los servicios
    // De esa determinada cuenta, si es null (No existe esa cuenta // No hay cuentas)m, muestra una lista vacia
    @GetMapping("/individual")
    public String facturacionIndividualPage(@RequestParam(required = false) Integer cuentaId, Model model) {

        // 1. Crea el objeto contenedor
        FacturacionFormDto facturaForm = new FacturacionFormDto();

        // 2. Si se seleccionó una cuenta, cargar sus servicios
        if (cuentaId != null) {
            List<ServicioDeLaCuentaDto> servicios = servicioDeLaCuentaServicio.obtenerServiciosPorCuentaPendiente(cuentaId);
            // Convertir a DTOs para el formulario
            List<ServicioSeleccionadoDto> serviciosDTO = new ArrayList<>();
            for (ServicioDeLaCuentaDto sdc : servicios) {
                ServicioSeleccionadoDto dto = new ServicioSeleccionadoDto();
                dto.setIdServicio(sdc.getIdServicioDeLaCuenta());
                dto.setCantidad(1);
                dto.setMontoUnitario(sdc.getServicio().getMontoServicio());
                dto.setNombreServicio(sdc.getServicio().getNombreServicio());
                dto.setSeleccionado(false);
                dto.setTieneCantidad(sdc.getServicio().isTieneCantidad());
                serviciosDTO.add(dto);
            }
            facturaForm.setItems(serviciosDTO);
            facturaForm.setCuentaId(cuentaId.longValue());
        } else {
            // Si no hay cuenta seleccionada, lista vacía
            facturaForm.setItems(new ArrayList<>());
        }

        // 3. Pasa el objeto al modelo
        model.addAttribute("facturaForm", facturaForm);
        model.addAttribute("Periodicidad", Periodicidad.values());
        model.addAttribute("cuentas", cuentaServicio.obtenerCuentas());
        
        // Atributos para mantener Seleccion
        // model.addAttribute("periodoSeleccionado", );
        model.addAttribute("cuentaSeleccionada", cuentaId); // para mantener selección

        return "facturacion-individual";
    }

    @PostMapping("/procesar")
    public String procesarFacturacionIndividual(
            @ModelAttribute("facturaForm") FacturacionFormDto facturaForm,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Filtrar solo los servicios seleccionados (checkbox marcado)
            List<ServicioSeleccionadoDto> serviciosSeleccionados = facturaForm.getItems()
                .stream()
                .filter(ServicioSeleccionadoDto::isSeleccionado)
                .collect(Collectors.toList());
            
            // Validar que se haya seleccionado al menos un servicio
            if (serviciosSeleccionados.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar al menos un servicio para facturar");
                return "redirect:/facturacion/individual?cuentaId=" + facturaForm.getCuentaId();
            }
            
            // Validar que se haya seleccionado una periodicidad
            if (facturaForm.getPeriodo() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un periodo");
                return "redirect:/facturacion/individual?cuentaId=" + facturaForm.getCuentaId();
            }
            
            // Llamar al servicio para procesar la facturación
            facturacionService.facturacionIndividual(serviciosSeleccionados, facturaForm.getPeriodo());
            
            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("success", "Facturación procesada correctamente");
            return "redirect:/facturacion/individual";
            
        } catch (Exception e) {
            // Manejo de errores
            redirectAttributes.addFlashAttribute("error", "Error al procesar la facturación: " + e.getMessage());
            return "redirect:/facturacion/individual?cuentaId=" + facturaForm.getCuentaId();
        }
    }
    
}
