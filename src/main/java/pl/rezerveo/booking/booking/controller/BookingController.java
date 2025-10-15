package pl.rezerveo.booking.booking.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rezerveo.booking.booking.dto.response.AvailableSlotsResponse;
import pl.rezerveo.booking.booking.dto.response.BookingListResponse;
import pl.rezerveo.booking.booking.service.BookingService;
import pl.rezerveo.booking.common.dto.PageResponse;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;
import pl.rezerveo.booking.openApi.booking.ApiBookSlotResponse;
import pl.rezerveo.booking.openApi.booking.ApiCancelBookingResponse;
import pl.rezerveo.booking.openApi.booking.ApiGetAvailableSlotsResponse;
import pl.rezerveo.booking.openApi.booking.ApiGetBookingListResponse;

import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static pl.rezerveo.booking.common.constant.Urls.BOOKINGS_URL;

@Slf4j
@RestController
@RequestMapping(BOOKINGS_URL)
@RequiredArgsConstructor
@Tag(name = "Zarządzanie rezerwacjami", description = "Operacje związane z rezerwacjami")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/available")
    @ApiGetAvailableSlotsResponse
    public PageResponse<AvailableSlotsResponse> getAvailableSlots(@PageableDefault(sort = "createdDate", direction = DESC) Pageable pageable) {
        return bookingService.getAvailableSlots(pageable);
    }

    @PostMapping("/{slotUuid}")
    @ApiBookSlotResponse
    public BaseResponse bookSlot(@PathVariable UUID slotUuid) {
        return bookingService.bookSlot(slotUuid);
    }

    @GetMapping
    @ApiGetBookingListResponse
    public PageResponse<BookingListResponse> getBookingList(@PageableDefault(sort = "createdDate", direction = DESC) Pageable pageable) {
        return bookingService.getBookingList(pageable);
    }

    @DeleteMapping("/{bookingUuid}")
    @ApiCancelBookingResponse
    public BaseResponse cancelBooking(@PathVariable UUID bookingUuid) {
        return bookingService.cancelBooking(bookingUuid);
    }
}