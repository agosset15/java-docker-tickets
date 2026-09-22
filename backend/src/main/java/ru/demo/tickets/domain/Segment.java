package ru.demo.tickets.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Check;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "segments",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_segments_ticket_serial",
                columnNames = {"ticket_number", "serial_number"}
        ),
        indexes = {
                @Index(name = "idx_segments_ticket", columnList = "ticket_number"),
                @Index(name = "idx_segments_document", columnList = "document_number")
        }
)
@Check(constraints = "gender in ('M','F') and serial_number > 0")
public class Segment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operation_code", nullable = false, length = 36)
    private String operationCode;

    @Column(name = "operation_time", nullable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime operationTime;

    @Column(name = "operation_timezone_minutes", nullable = false)
    private short operationTimezoneMinutes;

    @Column(name = "operation_place", nullable = false, length = 100)
    private String operationPlace;

    @Column(name = "passenger_name", nullable = false, length = 80)
    private String passengerName;

    @Column(name = "passenger_surname", nullable = false, length = 80)
    private String passengerSurname;

    @Column(name = "passenger_patronymic", length = 80)
    private String passengerPatronymic;

    @Column(name = "document_type", nullable = false, length = 10)
    private String documentType;

    @Column(name = "document_number", nullable = false, length = 40)
    private String documentNumber;

    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    @Column(name = "gender", nullable = false, length = 1)
    private String gender;

    @Column(name = "passenger_type", nullable = false, length = 30)
    private String passengerType;

    @Column(name = "ticket_number", nullable = false, length = 13)
    private String ticketNumber;

    @Column(name = "ticket_type", nullable = false)
    private int ticketType;

    @Column(name = "serial_number", nullable = false)
    private int serialNumber;

    @Column(name = "airline_code", nullable = false, length = 3)
    private String airlineCode;

    @Column(name = "flight_number", nullable = false)
    private int flightNumber;

    @Column(name = "depart_place", nullable = false, length = 3)
    private String departPlace;

    @Column(name = "depart_datetime", nullable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime departDatetime;

    @Column(name = "depart_timezone_minutes", nullable = false)
    private short departTimezoneMinutes;

    @Column(name = "arrive_place", nullable = false, length = 3)
    private String arrivePlace;

    @Column(name = "arrive_datetime", nullable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime arriveDatetime;

    @Column(name = "arrive_timezone_minutes", nullable = false)
    private short arriveTimezoneMinutes;

    @Column(name = "pnr_id", nullable = false, length = 12)
    private String pnrId;

    @Column(name = "refunded", nullable = false)
    private boolean refunded;

    @Column(name = "refund_time", columnDefinition = "timestamp with time zone")
    private OffsetDateTime refundTime;

    @Column(name = "refund_timezone_minutes")
    private Short refundTimezoneMinutes;

    public Segment() {
    }

    public Long getId() { return id; }
    public String getOperationCode() { return operationCode; }
    public void setOperationCode(String operationCode) { this.operationCode = operationCode; }
    public OffsetDateTime getOperationTime() { return operationTime; }
    public void setOperationTime(OffsetDateTime operationTime) { this.operationTime = operationTime; }
    public short getOperationTimezoneMinutes() { return operationTimezoneMinutes; }
    public void setOperationTimezoneMinutes(short operationTimezoneMinutes) { this.operationTimezoneMinutes = operationTimezoneMinutes; }
    public String getOperationPlace() { return operationPlace; }
    public void setOperationPlace(String operationPlace) { this.operationPlace = operationPlace; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getPassengerSurname() { return passengerSurname; }
    public void setPassengerSurname(String passengerSurname) { this.passengerSurname = passengerSurname; }
    public String getPassengerPatronymic() { return passengerPatronymic; }
    public void setPassengerPatronymic(String passengerPatronymic) { this.passengerPatronymic = passengerPatronymic; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    public LocalDate getBirthdate() { return birthdate; }
    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPassengerType() { return passengerType; }
    public void setPassengerType(String passengerType) { this.passengerType = passengerType; }
    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    public int getTicketType() { return ticketType; }
    public void setTicketType(int ticketType) { this.ticketType = ticketType; }
    public int getSerialNumber() { return serialNumber; }
    public void setSerialNumber(int serialNumber) { this.serialNumber = serialNumber; }
    public String getAirlineCode() { return airlineCode; }
    public void setAirlineCode(String airlineCode) { this.airlineCode = airlineCode; }
    public int getFlightNumber() { return flightNumber; }
    public void setFlightNumber(int flightNumber) { this.flightNumber = flightNumber; }
    public String getDepartPlace() { return departPlace; }
    public void setDepartPlace(String departPlace) { this.departPlace = departPlace; }
    public OffsetDateTime getDepartDatetime() { return departDatetime; }
    public void setDepartDatetime(OffsetDateTime departDatetime) { this.departDatetime = departDatetime; }
    public short getDepartTimezoneMinutes() { return departTimezoneMinutes; }
    public void setDepartTimezoneMinutes(short departTimezoneMinutes) { this.departTimezoneMinutes = departTimezoneMinutes; }
    public String getArrivePlace() { return arrivePlace; }
    public void setArrivePlace(String arrivePlace) { this.arrivePlace = arrivePlace; }
    public OffsetDateTime getArriveDatetime() { return arriveDatetime; }
    public void setArriveDatetime(OffsetDateTime arriveDatetime) { this.arriveDatetime = arriveDatetime; }
    public short getArriveTimezoneMinutes() { return arriveTimezoneMinutes; }
    public void setArriveTimezoneMinutes(short arriveTimezoneMinutes) { this.arriveTimezoneMinutes = arriveTimezoneMinutes; }
    public String getPnrId() { return pnrId; }
    public void setPnrId(String pnrId) { this.pnrId = pnrId; }
    public boolean isRefunded() { return refunded; }
    public void setRefunded(boolean refunded) { this.refunded = refunded; }
    public OffsetDateTime getRefundTime() { return refundTime; }
    public void setRefundTime(OffsetDateTime refundTime) { this.refundTime = refundTime; }
    public Short getRefundTimezoneMinutes() { return refundTimezoneMinutes; }
    public void setRefundTimezoneMinutes(Short refundTimezoneMinutes) { this.refundTimezoneMinutes = refundTimezoneMinutes; }
}
