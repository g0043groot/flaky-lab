package com.example.orders;

import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    public record ItemRequest(String sku, int quantity, long unitPriceCents) {}

    private final OrderService service;

    public OrderController(OrderService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create() {
        Order o = service.create();
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", o.getId(), "status", o.getStatus()));
    }

    @PostMapping("/{id}/items")
    public Map<String, Object> addItem(@PathVariable String id, @RequestBody ItemRequest req) {
        Order o = service.addItem(id, req.sku(), req.quantity(), req.unitPriceCents());
        return Map.of("id", o.getId(), "items", o.getItems().size(), "subtotalCents", o.subtotalCents());
    }

    @GetMapping("/{id}/total")
    public Map<String, Object> total(@PathVariable String id) {
        return Map.of("id", id, "totalCents", service.total(id));
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(NoSuchElementException e) { return Map.of("error", e.getMessage()); }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(RuntimeException e) { return Map.of("error", e.getMessage()); }
}
