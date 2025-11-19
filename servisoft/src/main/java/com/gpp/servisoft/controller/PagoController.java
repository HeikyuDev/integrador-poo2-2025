package com.gpp.servisoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gpp.servisoft.model.dto.FacturacionDTO;
import com.gpp.servisoft.model.dto.PagoDto;
import com.gpp.servisoft.service.CuentaService;
import com.gpp.servisoft.service.FacturacionService;
import com.gpp.servisoft.service.PagoService;

@Controller
@RequestMapping("/pago")
public class PagoController {

    @Autowired
    private CuentaService cuentaServicio;

    @Autowired
    private FacturacionService facturacionService;

    @Autowired
    private PagoService pagoService;

    /**
     * Muestra la lista de facturas pendientes de pago con filtros.
     * GET /pago/vista
     */
    @GetMapping("/vista")
    public String consultaFacturacion(
            @RequestParam(name = "cuentaId", required = false) Integer cuentaId,
            @RequestParam(name = "comprobante", required = false) String comprobante,
            @PageableDefault(size = 10, sort = "fechaEmision", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        try {
            // Cargar datos para los filtros (el dropdown de cuentas)
            model.addAttribute("cuentas", cuentaServicio.obtenerCuentas());

            // Cargar la PÁGINA de facturas pendientes filtrada
            Page<FacturacionDTO> paginaDto = facturacionService.obtenerFacturasPendientes(cuentaId, comprobante, pageable);

            // Pasar el objeto PAGE a la vista
            model.addAttribute("paginaDeFacturas", paginaDto);

            // Pasar los filtros para los links de paginación
            model.addAttribute("cuentaId", cuentaId);
            model.addAttribute("comprobante", comprobante);

            return "pagos/vista";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar facturas pendientes: " + e.getMessage());
            return "pagos/vista";
        }
    }

    /**
     * Muestra el formulario de pago total para una factura específica.
     * GET /pago/procesar/{id}
     * 
     * @param id ID de la factura a pagar
     * @param model Modelo para pasar datos a la vista
     * @return Vista del formulario de pago (pagos/total.html)
     */
    @GetMapping("/procesar/{id}")
    public String mostrarFormularioPago(
            @PathVariable Integer id,
            Model model) {
        
        try {
            // Obtener la factura por ID
            FacturacionDTO factura = facturacionService.obtenerFacturaPorId(id);
            
            // Pasar la factura a la vista
            model.addAttribute("factura", factura);
            
            return "pagos/total";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Factura no encontrada: " + e.getMessage());
            return "redirect:/pago/vista";
        }
    }

    /**
     * Procesa el pago total de una factura.
     * POST /pago/procesar
     * 
     * @param pagoDto DTO con los datos del pago (método, factura)
     * @param redirectAttributes Para pasar mensajes a la siguiente página
     * @return Redirección a la vista de gestión de pagos
     */
    @PostMapping("/procesar")
    public String procesarPago(
            PagoDto pagoDto,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Obtener la factura para conseguir el monto
            FacturacionDTO factura = facturacionService.obtenerFacturaPorId(pagoDto.getIdFacturacion());
            
            // Asignar el monto de la factura al DTO
            pagoDto.setMonto(factura.getMontoTotal());
            
            // Procesar el pago usando el servicio
            pagoService.procesarPagoTotal(pagoDto);
            
            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("success", 
                "Pago procesado exitosamente. Monto: $" + String.format("%.2f", pagoDto.getMonto()));
            
            return "redirect:/pago/vista";
        } catch (IllegalArgumentException e) {
            // Error de validación
            redirectAttributes.addFlashAttribute("error", 
                "Error al procesar el pago: " + e.getMessage());
            
            return "redirect:/pago/vista";
        } catch (Exception e) {
            // Error inesperado
            redirectAttributes.addFlashAttribute("error", 
                "Error inesperado al procesar el pago: " + e.getMessage());
            
            return "redirect:/pago/vista";
        }
    }

    /**
     * Muestra el formulario de pago parcial para una factura específica.
     * GET /pago/parcial/{id}
     * 
     * @param id ID de la factura a pagar parcialmente
     * @param model Modelo para pasar datos a la vista
     * @return Vista del formulario de pago parcial (pagos/parcial.html)
     */
    @GetMapping("/parcial/{id}")
    public String mostrarFormularioPagoParcial(
            @PathVariable Integer id,
            Model model) {
        
        try {
            // Obtener la factura por ID
            FacturacionDTO factura = facturacionService.obtenerFacturaPorId(id);
            
            // Pasar la factura a la vista
            model.addAttribute("factura", factura);
            
            return "pagos/parcial";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Factura no encontrada: " + e.getMessage());
            return "redirect:/pago/vista";
        }
    }

    /**
     * Procesa el pago parcial de una factura.
     * POST /pago/procesar-parcial
     * 
     * @param pagoDto DTO con los datos del pago (método, factura, monto)
     * @param redirectAttributes Para pasar mensajes a la siguiente página
     * @return Redirección a la vista de gestión de pagos
     */
    @PostMapping("/procesar-parcial")
    public String procesarPagoParcial(
            PagoDto pagoDto,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Procesar el pago parcial usando el servicio
            pagoService.procesarPagoParcial(pagoDto);
            
            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("success", 
                "Pago parcial procesado exitosamente. Monto: $" + String.format("%.2f", pagoDto.getMonto()));
            
            return "redirect:/pago/vista";
        } catch (IllegalArgumentException e) {
            // Error de validación
            redirectAttributes.addFlashAttribute("error", 
                "Error al procesar el pago: " + e.getMessage());
            
            return "redirect:/pago/vista";
        } catch (Exception e) {
            // Error inesperado
            redirectAttributes.addFlashAttribute("error", 
                "Error inesperado al procesar el pago: " + e.getMessage());
            
            return "redirect:/pago/vista";
        }
    }

}