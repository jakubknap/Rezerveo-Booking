package pl.rezerveo.booking.booking.dto.response;

import pl.rezerveo.booking.slot.enumerate.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AvailableSlotsResponse(
        UUID uuid,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String serviceType,
        String mechanicName
) {
    public AvailableSlotsResponse(UUID uuid,
                                  LocalDate date,
                                  LocalTime startTime,
                                  LocalTime endTime,
                                  ServiceType serviceType,
                                  String mechanicFirstName,
                                  String mechanicLastName) {
        this(uuid, date, startTime, endTime, serviceType.getDescription(), (mechanicFirstName + " " + mechanicLastName));
    }
}