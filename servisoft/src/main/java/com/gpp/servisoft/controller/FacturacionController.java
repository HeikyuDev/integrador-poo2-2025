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
import com.gpp.servisoft.model.dto.ServicioDeLaCuentaDto;
import com.gpp.servisoft.model.dto.ServicioSeleccionadoDto;
import com.gpp.servisoft.model.enums.Periodicidad;
import com.gpp.servisoft.services.CuentaServicio;
import com.gpp.servisoft.services.FacturacionService;
import com.gpp.servisoft.services.ServicioDeLaCuentaService;

/**
 * Controlador ligero para servir páginas Thymeleaf.
 *
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

        return "facturacion-individual";
    }

    @PostMapping("/procesar")
    public String procesarFacturacionIndividual(
            @ModelAttribute("facturaForm") FacturacionFormDto facturaForm,
            RedirectAttributes redirectAttributes) {

        // Validamos que haya elegido una cuenta
        if (facturaForm.getCuentaId() == null) {
            // Tu ControllerAdvice atrapará esto
            throw new IllegalArgumentException("Debe seleccionar una Cuenta para Facturar");
        }

        // 1. Filtrar
        List<ServicioSeleccionadoDto> serviciosSeleccionados = facturaForm.getItems()
                .stream()
                .filter(ServicioSeleccionadoDto::isSeleccionado)
                .collect(Collectors.toList());

        // 2. Validar
        if (serviciosSeleccionados.isEmpty()) {
            // Tu ControllerAdvice atrapará esto
            throw new IllegalArgumentException("Debe seleccionar al menos un servicio para facturar");
        }

        if (facturaForm.getPeriodo() == null) {
            // Tu ControllerAdvice atrapará esto
            throw new IllegalArgumentException("Debe seleccionar un periodo");
        }

        // 3. Si todo está bien, procesar:
        try {
            facturacionService.facturacionIndividual(serviciosSeleccionados, facturaForm.getPeriodo());
        } catch (Exception e) {
            // Si el servicio falla, también lo lanzamos al ControllerAdvice
            throw new RuntimeException("Error al procesar la facturación: " + e.getMessage());
        }

        // 4. Mensaje de éxito
        redirectAttributes.addFlashAttribute("success", "Facturación procesada correctamente");
        return "redirect:/facturacion/individual";
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

        return "facturacion-consulta";
    }

    @GetMapping("/detalle/{id}")
    public String verDetalleFactura(@PathVariable Integer id, Model model) {
        FacturacionDTO factura = facturacionService.obtenerFacturaPorId(id);

        if (factura == null) {
            throw new IllegalArgumentException("Factura no encontrada");
        }

        model.addAttribute("factura", factura);
        return "facturacion-detalle";
    }

}
