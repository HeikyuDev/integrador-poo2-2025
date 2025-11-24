package com.gpp.servisoft.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.gpp.servisoft.exceptions.ExcepcionNegocio;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Manejo global de errores para páginas Thymeleaf y APIs REST.
 *
 * - Si la petición es API (Accept JSON, XHR o path /api), responde JSON.
 * - Si es página, guarda un flash "error" y redirige al referer o /home.
 */
@ControllerAdvice
@SuppressWarnings("null")
public class ControllerAdviceGlobal {

    @ModelAttribute("appName")
    public String appName() {
        return "ServiSoft";
    }

    // ==== Validaciones (DTO @Valid) → JSON con detalles ====
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, Object> body = baseBody(request, HttpStatus.BAD_REQUEST, "Datos inválidos");

        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        body.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ==== Excepciones de negocio (ExcepcionNegocio) ====
    @ExceptionHandler(ExcepcionNegocio.class)
    public Object handleExcepcionNegocio(ExcepcionNegocio ex, HttpServletRequest request, HttpServletResponse response) {
        if (isApiRequest(request)) {
            Map<String, Object> body = baseBody(request, HttpStatus.BAD_REQUEST, ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

        // Páginas: usar FlashMap para mostrar mensaje y redirigir al referer o /home
        FlashMap flash = RequestContextUtils.getOutputFlashMap(request);
        if (flash != null) {
            flash.put("error", ex.getMessage() != null ? ex.getMessage() : "Ocurrió un error en el negocio");
            var manager = RequestContextUtils.getFlashMapManager(request);
            if (manager != null) {
                manager.saveOutputFlashMap(flash, request, response);
            }
        }

        String target = refererOrDefault(request, "/home");
        return new ModelAndView("redirect:" + target);
    }

    // ==== Excepciones comunes (IllegalArgumentException, IllegalStateException, RuntimeException) ====
    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class, RuntimeException.class })
    public Object handleBusiness(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        if (isApiRequest(request)) {
            Map<String, Object> body = baseBody(request, HttpStatus.BAD_REQUEST, ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

        // Páginas: usar FlashMap para mostrar mensaje y redirigir al referer o /home
        FlashMap flash = RequestContextUtils.getOutputFlashMap(request);
        if (flash != null) {
            flash.put("error", ex.getMessage() != null ? ex.getMessage() : "Ocurrió un error inesperado");
            var manager = RequestContextUtils.getFlashMapManager(request);
            if (manager != null) {
                manager.saveOutputFlashMap(flash, request, response);
            }
        }

        String target = refererOrDefault(request, "/home");
        return new ModelAndView("redirect:" + target);
    }

    // ==== Helpers ====
    private boolean isApiRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        String xhr = request.getHeader("X-Requested-With");
        String uri = request.getRequestURI();
        return (accept != null && accept.contains("application/json"))
                || (xhr != null && xhr.equalsIgnoreCase("XMLHttpRequest"))
                || (uri != null && uri.startsWith("/api"));
    }

    private Map<String, Object> baseBody(HttpServletRequest request, HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getRequestURI());
        return body;
    }

    private String refererOrDefault(HttpServletRequest request, String def) {
        String ref = request.getHeader("Referer");
        return (ref != null && !ref.isBlank()) ? ref : def;
    }
}
