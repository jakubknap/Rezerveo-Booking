package pl.rezerveo.booking.booking.service;

import org.springframework.data.domain.Pageable;
import pl.rezerveo.booking.booking.dto.response.AvailableSlotsResponse;
import pl.rezerveo.booking.booking.dto.response.BookingListResponse;
import pl.rezerveo.booking.common.dto.PageResponse;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;

import java.util.UUID;

public interface BookingService {

    PageResponse<AvailableSlotsResponse> getAvailableSlots(Pageable pageable);

    BaseResponse bookSlot(UUID slotUuid);

    PageResponse<BookingListResponse> getBookingList(Pageable pageable);

    BaseResponse cancelBooking(UUID bookingUuid);
}