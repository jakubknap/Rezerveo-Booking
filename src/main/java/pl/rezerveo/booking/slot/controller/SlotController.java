package pl.rezerveo.booking.slot.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rezerveo.booking.booking.service.BookingService;
import pl.rezerveo.booking.common.dto.PageResponse;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;
import pl.rezerveo.booking.openApi.slot.ApiCancelBookingByMechanicResponse;
import pl.rezerveo.booking.openApi.slot.ApiCancelSlotResponse;
import pl.rezerveo.booking.openApi.slot.ApiCreateSlotResponse;
import pl.rezerveo.booking.openApi.slot.ApiGetMechanicSlotsResponse;
import pl.rezerveo.booking.slot.dto.request.CreateSlotRequest;
import pl.rezerveo.booking.slot.dto.response.MechanicSlotsResponse;
import pl.rezerveo.booking.slot.service.SlotService;

import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static pl.rezerveo.booking.common.constant.Urls.SLOTS_URL;

@Slf4j
@RestController
@RequestMapping(SLOTS_URL)
@RequiredArgsConstructor
@Tag(name = "Zarządzanie slotami przez mechnika", description = "Operacje związane ze slotami")
public class SlotController {

    private final SlotService slotService;
    private final BookingService bookingService;

    @PostMapping
    @ApiCreateSlotResponse
    public BaseResponse createSlot(@RequestBody @Valid CreateSlotRequest request) {
        return slotService.createSlot(request);
    }

    @GetMapping
    @ApiGetMechanicSlotsResponse
    public PageResponse<MechanicSlotsResponse> getMechanicSlots(@PageableDefault(sort = "createdDate", direction = DESC) Pageable pageable) {
        return slotService.getMechanicSlots(pageable);
    }

    @DeleteMapping("/{slotUuid}")
    @ApiCancelSlotResponse
    public BaseResponse cancelSlot(@PathVariable UUID slotUuid) {
        return slotService.cancelSlot(slotUuid);
    }

    @DeleteMapping("/{slotUuid}/bookings/{bookingUuid}")
    @ApiCancelBookingByMechanicResponse
    public BaseResponse cancelBooking(@PathVariable UUID slotUuid, @PathVariable UUID bookingUuid) {
        return bookingService.cancelBookingByMechanic(slotUuid, bookingUuid);
    }
}