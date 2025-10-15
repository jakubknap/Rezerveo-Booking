package pl.rezerveo.booking.slot.dto.response;

import pl.rezerveo.booking.slot.enumerate.ServiceType;
import pl.rezerveo.booking.slot.enumerate.SlotStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record MechanicSlotsResponse(
        UUID uuid,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String serviceType,
        SlotStatus status
) {
    public MechanicSlotsResponse(UUID uuid,
                                 LocalDate date,
                                 LocalTime startTime,
                                 LocalTime endTime,
                                 ServiceType serviceType,
                                 SlotStatus status) {
        this(uuid, date, startTime, endTime, serviceType.getDescription(), status);
    }
}