package ru.demo.tickets.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.demo.tickets.domain.Segment;
import ru.demo.tickets.dto.OperationResponse;
import ru.demo.tickets.dto.PassengerRequest;
import ru.demo.tickets.dto.RefundRequest;
import ru.demo.tickets.dto.RouteRequest;
import ru.demo.tickets.dto.SaleRequest;
import ru.demo.tickets.dto.SegmentResponse;
import ru.demo.tickets.repository.SegmentRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {
    private final SegmentRepository repository;

    public TicketService(SegmentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public OperationResponse sale(SaleRequest request) {
        validateDocument(request.passenger());
        String operationCode = UUID.randomUUID().toString();

        try {
            for (int index = 0; index < request.routes().size(); index++) {
                Segment segment = toSegment(request, request.routes().get(index), index + 1, operationCode);
                repository.saveAndFlush(segment);
            }
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("Билет с номером " + request.passenger().ticketNumber() + " уже существует");
        }

        return new OperationResponse(operationCode, request.passenger().ticketNumber(), request.routes().size(), "SOLD");
    }

    @Transactional
    public OperationResponse refund(RefundRequest request) {
        List<Segment> segments = repository.findByTicketNumberForUpdate(request.ticketNumber());
        if (segments.isEmpty()) {
            throw new ConflictException("Билет с номером " + request.ticketNumber() + " не найден");
        }
        if (segments.stream().anyMatch(Segment::isRefunded)) {
            throw new ConflictException("Билет с номером " + request.ticketNumber() + " уже возвращен");
        }

        short offsetMinutes = offsetMinutes(request.operationTime());
        segments.forEach(segment -> {
            segment.setRefunded(true);
            segment.setRefundTime(request.operationTime());
            segment.setRefundTimezoneMinutes(offsetMinutes);
        });
        repository.saveAll(segments);

        return new OperationResponse(UUID.randomUUID().toString(), request.ticketNumber(), segments.size(), "REFUNDED");
    }

    @Transactional(readOnly = true)
    public List<SegmentResponse> search(String ticketNumber, String documentNumber, boolean allPassengerTickets) {
        List<Segment> segments;
        if (ticketNumber != null && !ticketNumber.isBlank()) {
            segments = allPassengerTickets
                    ? repository.findAllPassengerTickets(ticketNumber)
                    : repository.findByTicketNumberOrderBySerialNumber(ticketNumber);
        } else if (documentNumber != null && !documentNumber.isBlank()) {
            segments = repository.findByDocumentNumberOrderByOperationTimeDescTicketNumberAscSerialNumberAsc(documentNumber);
        } else {
            segments = repository.findAll();
        }
        return segments.stream().map(this::toResponse).toList();
    }

    private void validateDocument(PassengerRequest passenger) {
        if ("00".equals(passenger.docType()) && !passenger.docNumber().matches("\\d{10}")) {
            throw new IllegalArgumentException("Для документа типа 00 номер должен состоять из 10 цифр");
        }
    }

    private Segment toSegment(SaleRequest request, RouteRequest route, int serialNumber, String operationCode) {
        PassengerRequest passenger = request.passenger();
        Segment segment = new Segment();
        segment.setOperationCode(operationCode);
        segment.setOperationTime(request.operationTime());
        segment.setOperationTimezoneMinutes(offsetMinutes(request.operationTime()));
        segment.setOperationPlace(request.operationPlace());
        segment.setPassengerName(passenger.name());
        segment.setPassengerSurname(passenger.surname());
        segment.setPassengerPatronymic(passenger.patronymic());
        segment.setDocumentType(passenger.docType());
        segment.setDocumentNumber(passenger.docNumber());
        segment.setBirthdate(passenger.birthdate());
        segment.setGender(passenger.gender());
        segment.setPassengerType(passenger.passengerType());
        segment.setTicketNumber(passenger.ticketNumber());
        segment.setTicketType(passenger.ticketType());
        segment.setSerialNumber(serialNumber);
        segment.setAirlineCode(route.airlineCode());
        segment.setFlightNumber(route.flightNum());
        segment.setDepartPlace(route.departPlace());
        segment.setDepartDatetime(route.departDatetime());
        segment.setDepartTimezoneMinutes(offsetMinutes(route.departDatetime()));
        segment.setArrivePlace(route.arrivePlace());
        segment.setArriveDatetime(route.arriveDatetime());
        segment.setArriveTimezoneMinutes(offsetMinutes(route.arriveDatetime()));
        segment.setPnrId(route.pnrId());
        segment.setRefunded(false);
        return segment;
    }

    private short offsetMinutes(OffsetDateTime time) {
        return (short) (time.getOffset().getTotalSeconds() / 60);
    }

    private SegmentResponse toResponse(Segment segment) {
        return new SegmentResponse(
                segment.getId(), segment.getOperationCode(), segment.getOperationTime(), segment.getOperationPlace(),
                segment.getPassengerName(), segment.getPassengerSurname(), segment.getPassengerPatronymic(),
                segment.getDocumentType(), segment.getDocumentNumber(), segment.getBirthdate(), segment.getGender(),
                segment.getPassengerType(), segment.getTicketNumber(), segment.getTicketType(), segment.getSerialNumber(),
                segment.getAirlineCode(), segment.getFlightNumber(), segment.getDepartPlace(), segment.getDepartDatetime(),
                segment.getArrivePlace(), segment.getArriveDatetime(), segment.getPnrId(), segment.isRefunded(),
                segment.getRefundTime()
        );
    }
}
