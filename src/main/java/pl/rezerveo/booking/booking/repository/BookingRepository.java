package pl.rezerveo.booking.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.rezerveo.booking.booking.dto.response.BookingListResponse;
import pl.rezerveo.booking.booking.model.Booking;

import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            SELECT new pl.rezerveo.booking.booking.dto.response.BookingListResponse(
                    b.uuid,
                    s.date,
                    s.startTime,
                    s.endTime,
                    s.serviceType,
                    m.firstName,
                    m.lastName,
                    b.status
            )
            FROM Booking b
                     JOIN b.slot s
                     JOIN s.mechanic m
            """)
    Page<BookingListResponse> findAllByClientUuid(Pageable pageable, UUID clientUuid);

    @Query("""
            SELECT b
            FROM Booking b
                     JOIN FETCH b.slot
            WHERE b.uuid = :bookingUuid
            """)
    Optional<Booking> findBookingWithSlotByUuid(UUID bookingUuid);
}