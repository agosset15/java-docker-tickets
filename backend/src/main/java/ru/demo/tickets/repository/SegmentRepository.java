package ru.demo.tickets.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.demo.tickets.domain.Segment;

import java.util.List;

public interface SegmentRepository extends JpaRepository<Segment, Long> {
    List<Segment> findByTicketNumberOrderBySerialNumber(String ticketNumber);

    List<Segment> findByDocumentNumberOrderByOperationTimeDescTicketNumberAscSerialNumberAsc(String documentNumber);

    @Query(value = """
            select s.* from segments s
            where s.document_number = (
                select s2.document_number from segments s2 where s2.ticket_number = :ticketNumber limit 1
            )
            order by s.operation_time desc, s.ticket_number, s.serial_number
            """, nativeQuery = true)
    List<Segment> findAllPassengerTickets(@Param("ticketNumber") String ticketNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Segment s where s.ticketNumber = :ticketNumber order by s.serialNumber")
    List<Segment> findByTicketNumberForUpdate(@Param("ticketNumber") String ticketNumber);
}
