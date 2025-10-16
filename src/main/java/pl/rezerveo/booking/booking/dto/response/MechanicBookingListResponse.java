package pl.rezerveo.booking.booking.dto.response;

import pl.rezerveo.booking.booking.enumerated.BookingStatus;
import pl.rezerveo.booking.slot.enumerate.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record MechanicBookingListResponse(
        UUID bookingUuid,
        UUID slotUuid,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String serviceType,
        String mechanicName,
        BookingStatus status
) {

    public MechanicBookingListResponse(UUID bookingUuid,
                                       UUID slotUuid,
                                       LocalDate date,
                                       LocalTime startTime,
                                       LocalTime endTime,
                                       ServiceType serviceType,
                                       String mechanicFirstName,
                                       String mechanicLastName,
                                       BookingStatus status) {
        this(bookingUuid, slotUuid, date, startTime, endTime, serviceType.getDescription(), (mechanicFirstName + " " + mechanicLastName), status);
    }
}