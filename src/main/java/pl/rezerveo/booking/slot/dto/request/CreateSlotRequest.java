package pl.rezerveo.booking.slot.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import pl.rezerveo.booking.slot.enumerate.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateSlotRequest(@NotNull
                                @FutureOrPresent
                                LocalDate date,

                                @NotNull
                                @FutureOrPresent
                                LocalTime startTime,

                                @NotNull
                                @FutureOrPresent
                                LocalTime endTime,

                                @NotNull
                                ServiceType serviceType) {}