package pl.rezerveo.booking.booking.dto.response;

import pl.rezerveo.booking.booking.enumerated.BookingStatus;
import pl.rezerveo.booking.slot.enumerate.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record BookingListResponse(
        UUID uuid,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        ServiceType serviceType,
        String mechanicName,
        BookingStatus status
) {

    public BookingListResponse(UUID uuid,
                               LocalDate date,
                               LocalTime startTime,
                               LocalTime endTime,
                               ServiceType serviceType,
                               String mechanicFirstName,
                               String mechanicLastName,
                               BookingStatus status) {
        this(uuid, date, startTime, endTime, serviceType, (mechanicFirstName + " " + mechanicLastName), status);
    }
}