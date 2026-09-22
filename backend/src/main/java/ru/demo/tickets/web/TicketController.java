package ru.demo.tickets.web;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.demo.tickets.dto.OperationResponse;
import ru.demo.tickets.dto.RefundRequest;
import ru.demo.tickets.dto.SaleRequest;
import ru.demo.tickets.dto.SegmentResponse;
import ru.demo.tickets.service.TicketService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TicketController {
    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @PostMapping("/process/sale")
    public ResponseEntity<OperationResponse> sale(@Valid @RequestBody SaleRequest request) {
        return ResponseEntity.ok(service.sale(request));
    }

    @PostMapping("/process/refund")
    public ResponseEntity<OperationResponse> refund(@Valid @RequestBody RefundRequest request) {
        return ResponseEntity.ok(service.refund(request));
    }

    @GetMapping("/segments")
    public List<SegmentResponse> search(
            @RequestParam(required = false) String ticketNumber,
            @RequestParam(required = false) String documentNumber,
            @RequestParam(defaultValue = "false") boolean allPassengerTickets
    ) {
        return service.search(ticketNumber, documentNumber, allPassengerTickets);
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
                "application", "tickets-backend",
                "status", "UP",
                "database", "PostgreSQL",
                "time", OffsetDateTime.now().toString()
        );
    }
}

