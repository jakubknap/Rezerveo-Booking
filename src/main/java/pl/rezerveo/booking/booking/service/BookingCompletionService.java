package pl.rezerveo.booking.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rezerveo.booking.booking.enumerated.BookingStatus;
import pl.rezerveo.booking.booking.model.Booking;
import pl.rezerveo.booking.booking.repository.BookingRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingCompletionService {

    private final BookingRepository bookingRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void completePastBookings() {
        log.info("Starting Booking Completion Cron");

        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();

        List<Booking> bookingsToComplete = bookingRepository.findAllByStatusAndSlotEndTimeBefore(BookingStatus.CONFIRMED, today, now);

        log.info("Found {} bookings to complete", bookingsToComplete.size());

        for (Booking booking : bookingsToComplete) {
            booking.setStatus(BookingStatus.COMPLETED);
            log.info("Booking [{}] set to COMPLETED", booking.getUuid());
        }

        bookingRepository.saveAll(bookingsToComplete);

        log.info("Booking Completion Cron finished");
    }
}