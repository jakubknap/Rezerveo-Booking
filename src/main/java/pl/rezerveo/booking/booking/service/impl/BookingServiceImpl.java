package pl.rezerveo.booking.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rezerveo.booking.booking.dto.response.AvailableSlotsResponse;
import pl.rezerveo.booking.booking.dto.response.BookingListResponse;
import pl.rezerveo.booking.booking.enumerated.BookingStatus;
import pl.rezerveo.booking.booking.model.Booking;
import pl.rezerveo.booking.booking.repository.BookingRepository;
import pl.rezerveo.booking.booking.service.BookingService;
import pl.rezerveo.booking.common.dto.PageResponse;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;
import pl.rezerveo.booking.exception.exception.ServiceException;
import pl.rezerveo.booking.slot.enumerate.SlotStatus;
import pl.rezerveo.booking.slot.model.Slot;
import pl.rezerveo.booking.slot.repository.SlotRepository;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E05001;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E05004;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E06000;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E06001;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.S00000;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.S00001;
import static pl.rezerveo.booking.security.util.SecurityUtils.getLoggedUser;
import static pl.rezerveo.booking.security.util.SecurityUtils.getLoggedUserUUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final SlotRepository slotRepository;
    private final BookingRepository bookingRepository;

    @Override
    public PageResponse<AvailableSlotsResponse> getAvailableSlots(Pageable pageable) {
        return PageResponse.of(slotRepository.findAvailableSlots(pageable));
    }

    @Override
    @Transactional
    public BaseResponse bookSlot(UUID slotUuid) {
        Slot slot = getSlotOrElseThrow(slotUuid);

        if (SlotStatus.AVAILABLE != slot.getStatus()) {
            throw new ServiceException(E05004);
        }

        Booking booking = Booking.builder()
                                 .uuid(randomUUID())
                                 .status(BookingStatus.CONFIRMED)
                                 .slot(slot)
                                 .client(getLoggedUser())
                                 .build();

        slot.setStatus(SlotStatus.BOOKED);
        bookingRepository.save(booking);
        slotRepository.save(slot);

        //TODO notyfikacja

        return new BaseResponse(S00000);
    }

    @Override
    public PageResponse<BookingListResponse> getBookingList(Pageable pageable) {
        Page<BookingListResponse> bookingList = bookingRepository.findAllByClientUuid(pageable, getLoggedUserUUID());
        return PageResponse.of(bookingList);
    }

    @Override
    @Transactional
    public BaseResponse cancelBooking(UUID bookingUuid) {
        Booking booking = getBookingWithSlotOrElseThrow(bookingUuid);

        if (BookingStatus.CONFIRMED != booking.getStatus()) {
            throw new ServiceException(E06001);
        }

        booking.setStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);

        Slot slot = booking.getSlot();
        if (SlotStatus.BOOKED == slot.getStatus()) {
            slot.setStatus(SlotStatus.AVAILABLE);
            slotRepository.save(slot);
        }

        //TODO notyfikacja
        return new BaseResponse(S00001);
    }

    private Slot getSlotOrElseThrow(UUID slotUuid) {
        return slotRepository.findByUuid(slotUuid)
                             .orElseThrow(() -> {
                                 log.error("Slot with UUID: [{}] not found", slotUuid);
                                 return new ServiceException(E05001);
                             });
    }

    private Booking getBookingWithSlotOrElseThrow(UUID bookingUuid) {
        return bookingRepository.findBookingWithSlotByUuid(bookingUuid)
                                .orElseThrow(() -> {
                                    log.error("Booking with UUID: [{}] not found", bookingUuid);
                                    return new ServiceException(E06000);
                                });
    }
}