package pl.rezerveo.booking.slot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.rezerveo.booking.slot.dto.response.MechanicSlotsResponse;
import pl.rezerveo.booking.slot.model.Slot;
import pl.rezerveo.booking.user.model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    @Query("""
            SELECT COUNT(s) > 0
            FROM Slot s
            WHERE s.mechanic = :mechanic
              AND s.date = :date
              AND (
                (:startTime BETWEEN s.startTime AND s.endTime)
                    OR (:endTime BETWEEN s.startTime AND s.endTime)
                    OR (s.startTime BETWEEN :startTime AND :endTime)
                    OR (s.endTime BETWEEN :startTime AND :endTime)
                )
            """)
    boolean existsByMechanicAndDateAndTimeOverlap(User mechanic, LocalDate date, LocalTime startTime, LocalTime endTime);

    @Query("""
            SELECT new pl.rezerveo.booking.slot.dto.response.MechanicSlotsResponse(
                   s.uuid,
                   s.date,
                   s.startTime,
                   s.endTime,
                   s.serviceType,
                   s.status
            )
            FROM Slot s
            WHERE s.mechanic.uuid = :mechanicUuid
            """)
    Page<MechanicSlotsResponse> getMechanicSlots(Pageable pageable, UUID mechanicUuid);

    @Query("""
            SELECT s
            FROM Slot s
                     JOIN FETCH s.mechanic
                     JOIN FETCH s.booking
            WHERE s.uuid = :slotUuid
            """)
    Optional<Slot> findSlotWithMechanicAndBookingByUuid(UUID slotUuid);
}