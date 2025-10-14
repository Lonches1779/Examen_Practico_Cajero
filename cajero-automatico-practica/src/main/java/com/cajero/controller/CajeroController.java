package com.cajero.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cajero.servicie.CajeroService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cajero")
@RequiredArgsConstructor
public class CajeroController {

    private final CajeroService service;

    public static record RetiroRequest(double monto) {}
    public static record RetiroResponse(boolean success, String message, List<Map<String, Object>> breakdown) {}

    @PostMapping("/retirar")
    public ResponseEntity<RetiroResponse> retirar(@RequestBody RetiroRequest req) {
        var resultado = service.retirar(req.monto());
        if (!resultado.success) {
            return ResponseEntity.badRequest().body(
                new RetiroResponse(false, resultado.message, List.of())
            );
        }

        List<Map<String, Object>> detalle = resultado.items.stream().map(i -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("tipo", i.cajero.getDenominacion()); // Billete o Moneda
            m.put("valor", i.cajero.getCentavos() / 100.0);
            m.put("cantidad", i.cantidad);
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(new RetiroResponse(true, resultado.message, detalle));
    }

 // Endpoint para consultar el inventario actual
    @GetMapping("/inventario")
    public List<Map<String, Object>> inventario() {
        return service.getInventario().stream()
                .map(c -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", c.getId());
                    m.put("nombre", c.getNombre());
                    m.put("tipo", c.getDenominacion());
                    m.put("valor", c.getCentavos() / 100.0);
                    m.put("cantidad", c.getCantidadTotal());
                    return m;
                })
                .collect(Collectors.toList());
    }

    //ENDPOINT POST: http://localhost:7575/api/cajero/resetear_cajero?token=admin123
    @PostMapping("/resetear_cajero")
    public ResponseEntity<?> reset(@RequestParam String token) {
        if (!token.equals("admin123")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Token inválido"));
        }
        service.reset();
        return ResponseEntity.ok(Map.of("status", "OK"));
    }

    
    
}
