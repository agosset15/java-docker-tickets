package ru.demo.tickets.service;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import ru.demo.tickets.dto.PassengerRequest;
import ru.demo.tickets.dto.RefundRequest;
import ru.demo.tickets.dto.RouteRequest;
import ru.demo.tickets.dto.SaleRequest;
import ru.demo.tickets.repository.SegmentRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TicketServiceTest {
    private final SegmentRepository repository = mock(SegmentRepository.class);
    private final TicketService service = new TicketService(repository);

    @Test
    void rejectsRussianPassportWithWrongNumber() {
        PassengerRequest passenger = passenger("123", "5552139265681");
        assertThrows(IllegalArgumentException.class, () -> service.sale(sale(passenger)));
    }

    @Test
    void convertsUniqueConstraintViolationToConflict() {
        when(repository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("duplicate"));
        assertThrows(ConflictException.class, () -> service.sale(sale(passenger("4510123456", "5552139265681"))));
    }

    @Test
    void rejectsRefundForMissingTicket() {
        when(repository.findByTicketNumberForUpdate("5552139265999")).thenReturn(List.of());
        RefundRequest request = new RefundRequest(
                "refund", OffsetDateTime.parse("2026-09-21T10:00:00+03:00"), "test", "5552139265999"
        );
        assertThrows(ConflictException.class, () -> service.refund(request));
    }

    private PassengerRequest passenger(String documentNumber, String ticketNumber) {
        return new PassengerRequest(
                "Ivan", "Ivanov", null, "00", documentNumber, LocalDate.parse("2001-07-12"),
                "M", "student", ticketNumber, 1
        );
    }

    private SaleRequest sale(PassengerRequest passenger) {
        return new SaleRequest(
                "sale", OffsetDateTime.parse("2026-09-21T10:00:00+03:00"), "test", passenger,
                List.of(new RouteRequest(
                        "SU", 101, "SVO", OffsetDateTime.parse("2026-10-01T10:00:00+03:00"),
                        "KZN", OffsetDateTime.parse("2026-10-01T11:30:00+03:00"), "ABC123"
                ))
        );
    }
}

