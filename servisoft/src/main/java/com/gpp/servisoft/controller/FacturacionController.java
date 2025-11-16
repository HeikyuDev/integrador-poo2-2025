package com.gpp.servisoft.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gpp.servisoft.model.dto.FacturacionDTO;
import com.gpp.servisoft.model.dto.FacturacionFormDto;
import com.gpp.servisoft.model.dto.FacturacionMasivaDto;
import com.gpp.servisoft.model.dto.ServicioDeLaCuentaDto;
import com.gpp.servisoft.model.dto.ServicioSeleccionadoDto;
import com.gpp.servisoft.model.enums.Periodicidad;
import com.gpp.servisoft.service.CuentaService;
import com.gpp.servisoft.service.FacturacionService;
import com.gpp.servisoft.service.ServicioDeLaCuentaService;

/**
 * Controlador ligero para servir páginas Thymeleaf.
 *
 */
@Controller
@RequestMapping("/facturacion")
public class FacturacionController {

    @Autowired
    private CuentaService cuentaServicio;

    @Autowired
    private ServicioDeLaCuentaService servicioDeLaCuentaServicio;

    @Autowired
    private FacturacionService facturacionService;

    // utiliza el id de la cuenta seleccionado en el Combo para mostrar los
    // servicios
    // De esa determinada cuenta, si es null (No existe esa cuenta // No hay
    // cuentas)m, muestra una lista vacia
    @GetMapping("/individual")
    public String facturacionIndividual(@RequestParam(required = false) Integer cuentaId, Model model) {

        // 1. Crea el objeto contenedor
        FacturacionFormDto facturaForm = new FacturacionFormDto();

        // 2. Si se seleccionó una cuenta, cargar sus servicios
        if (cuentaId != null) {
            List<ServicioDeLaCuentaDto> servicios = servicioDeLaCuentaServicio
                    .obtenerServiciosPorCuentaPendiente(cuentaId);
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

        return "facturacion/facturacion-individual";
    }

    @PostMapping("/procesar")
    public String procesarFacturacionIndividual(
            @ModelAttribute("facturaForm") FacturacionFormDto facturaForm,
            RedirectAttributes redirectAttributes) {

        try {
            // Validamos que haya elegido una cuenta
            if (facturaForm.getCuentaId() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar una cuenta para facturar");
                return "redirect:/facturacion/individual";
            }

            // 1. Filtrar servicios seleccionados
            List<ServicioSeleccionadoDto> serviciosSeleccionados = facturaForm.getItems()
                    .stream()
                    .filter(ServicioSeleccionadoDto::isSeleccionado)
                    .collect(Collectors.toList());

            // 2. Validar que haya servicios seleccionados
            if (serviciosSeleccionados.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar al menos un servicio para facturar");
                return "redirect:/facturacion/individual?cuentaId=" + facturaForm.getCuentaId();
            }

            // 3. Validar período
            if (facturaForm.getPeriodo() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un período");
                return "redirect:/facturacion/individual?cuentaId=" + facturaForm.getCuentaId();
            }

            // 4. Procesar facturación individual
            facturacionService.facturacionIndividual(serviciosSeleccionados, facturaForm.getPeriodo());

            // 5. Mensaje de éxito
            redirectAttributes.addFlashAttribute("success", "Facturación procesada correctamente");
            return "redirect:/facturacion/individual";
            
        } catch (Exception e) {
            // Capturar cualquier excepción y mostrar mensaje de error
            redirectAttributes.addFlashAttribute("error", "Error al procesar la facturación: " + e.getMessage());
            return "redirect:/facturacion/individual";
        }
    }

    @GetMapping("/consulta")
    public String consultaFacturacion(
            @RequestParam(name = "cuentaId", required = false) Integer cuentaId,
            @RequestParam(name = "comprobante", required = false) String comprobante,
            @PageableDefault(size = 10, sort = "fechaEmision", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        // 2. Cargar datos para los filtros (el dropdown de cuentas)
        model.addAttribute("cuentas", cuentaServicio.obtenerCuentas());

        // 3. Cargar la PÁGINA de facturas filtrada
        // (Llama al método que devuelve un Page<FacturacionDTO>)
        Page<FacturacionDTO> paginaDto = facturacionService.obtenerFacturas(cuentaId, comprobante, pageable);

        // 4. Pasar el objeto PAGE (no una List) a la vista
        model.addAttribute("paginaDeFacturas", paginaDto);

        // (Opcional pero recomendado: pasa los filtros para los links de paginación)
        model.addAttribute("cuentaId", cuentaId);
        model.addAttribute("comprobante", comprobante);

        return "facturacion/facturacion-consulta";
    }

    @GetMapping("/detalle/{id}")
    public String verDetalleFactura(@PathVariable Integer id, Model model) {
        FacturacionDTO factura = facturacionService.obtenerFacturaPorId(id);

        if (factura == null) {
            throw new IllegalArgumentException("Factura no encontrada");
        }

        model.addAttribute("factura", factura);
        return "facturacion/facturacion-detalle";
    }

    // ================= FACTURACION MASIVA =====================

    @GetMapping("/masiva")
    public String facturacionMasiva(Model model,
    @PageableDefault(size = 10, sort = "fechaEmision", direction = Sort.Direction.DESC) Pageable pageable) {

        // 1. Crea el objeto contenedor
        FacturacionFormDto facturaForm = new FacturacionFormDto();

        // 2. Obtengo todos las Facturaciones Masivas Existenetes
        Page<FacturacionMasivaDto> facturacionesMasivasDto = facturacionService.obtenerFacturacionesMasivas(pageable);

        // 3. Pasa el objeto al modelo
        model.addAttribute("facturaForm", facturaForm);
        model.addAttribute("paginaDeFacturasMasivas", facturacionesMasivasDto);
        model.addAttribute("Periodicidad", Periodicidad.values());

        // Atributos para mantener Seleccion

        return "facturacion/facturacion-masiva";
    }
    
    @PostMapping("/procesar/masiva")
    public String procesarFacturacionMasiva(
            @ModelAttribute("facturaForm") FacturacionFormDto facturaForm,
            RedirectAttributes redirectAttributes) {

        try {
            // Validación de período
            if (facturaForm.getPeriodo() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un período");
                return "redirect:/facturacion/masiva";
            }

            // Procesar facturación masiva
            facturacionService.facturacionMasiva(facturaForm.getPeriodo());

            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("success", "Facturación masiva procesada correctamente");
            return "redirect:/facturacion/masiva";
            
        } catch (IllegalArgumentException e) {
            // Capturar errores de validación de negocio
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/facturacion/masiva";
        } catch (Exception e) {
            // Capturar cualquier otra excepción
            redirectAttributes.addFlashAttribute("error", "No se pudieron procesar las facturas masivas en este momento");
            return "redirect:/facturacion/masiva";
        }
    }

    @GetMapping("/masiva/{id}")
    public String verDetalleFacturacionMasiva(@PathVariable Integer id, Model model) {
        FacturacionMasivaDto facturacionMasivaDto = facturacionService.obtenerFacturacionMasivaPorId(id);
        
        if (facturacionMasivaDto == null) {
            throw new IllegalArgumentException("Facturación masiva no encontrada");
        }
        
        model.addAttribute("facturacionMasiva", facturacionMasivaDto);
        return "facturacion/facturacion-masiva-detalle";
    }
}
