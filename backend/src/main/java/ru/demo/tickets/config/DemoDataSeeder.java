package ru.demo.tickets.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.demo.tickets.dto.PassengerRequest;
import ru.demo.tickets.dto.RouteRequest;
import ru.demo.tickets.dto.SaleRequest;
import ru.demo.tickets.repository.SegmentRepository;
import ru.demo.tickets.service.TicketService;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@ConditionalOnProperty(name = "demo.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DemoDataSeeder implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final SegmentRepository repository;
    private final TicketService service;

    public DemoDataSeeder(SegmentRepository repository, TicketService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            return;
        }

        PassengerRequest passenger = new PassengerRequest(
                "Дмитрий", "Макаров", "Павлович", "00", "3108111434",
                LocalDate.parse("2001-07-12"), "M", "youth", "5552139265672", 1
        );
        service.sale(new SaleRequest(
                "sale", OffsetDateTime.parse("2026-09-01T03:25:00+03:00"), "Aeroflot", passenger,
                List.of(
                        new RouteRequest("SU", 1701, "VVO", OffsetDateTime.parse("2026-10-02T09:20:00+10:00"),
                                "SVO", OffsetDateTime.parse("2026-10-02T11:25:00+03:00"), "THALSZ"),
                        new RouteRequest("SU", 1702, "SVO", OffsetDateTime.parse("2026-10-05T16:10:00+03:00"),
                                "VVO", OffsetDateTime.parse("2026-10-06T07:40:00+10:00"), "THALSZ")
                )
        ));

        PassengerRequest secondTicket = new PassengerRequest(
                passenger.name(), passenger.surname(), passenger.patronymic(), passenger.docType(), passenger.docNumber(),
                passenger.birthdate(), passenger.gender(), passenger.passengerType(), "5552139265673", passenger.ticketType()
        );
        service.sale(new SaleRequest(
                "sale", OffsetDateTime.parse("2026-09-02T10:00:00+03:00"), "S7", secondTicket,
                List.of(new RouteRequest("S7", 520, "OVB", OffsetDateTime.parse("2026-11-10T12:00:00+07:00"),
                        "DME", OffsetDateTime.parse("2026-11-10T12:30:00+03:00"), "DEMO42"))
        ));
        log.info("Созданы демонстрационные билеты 5552139265672 и 5552139265673");
    }
}

