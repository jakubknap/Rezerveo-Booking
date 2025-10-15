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
import pl.rezerveo.booking.security.util.SecurityUtils;
import pl.rezerveo.booking.slot.enumerate.SlotStatus;
import pl.rezerveo.booking.slot.model.Slot;
import pl.rezerveo.booking.slot.repository.SlotRepository;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E05001;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E05004;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E06000;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E06001;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E06003;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E06004;
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
        Slot slot = getSlotWithUuidOrElseThrow(slotUuid);

        validateSlot(slot);

        Booking booking = buildBooking(slot);

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
        Booking booking = getBookingWithClientAndSlotOrElseThrow(bookingUuid);

        validateBooking(booking);

        booking.setStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);

        Slot slot = booking.getSlot();

        boolean stillBooked = slot.getBookings()
                                  .stream()
                                  .anyMatch(b -> b.getStatus() == BookingStatus.CONFIRMED);

        if (!stillBooked) {
            slot.setStatus(SlotStatus.AVAILABLE);
            slotRepository.save(slot);
        }

        //TODO notyfikacja
        return new BaseResponse(S00001);
    }

    private Slot getSlotWithUuidOrElseThrow(UUID slotUuid) {
        return slotRepository.findSlotWithMechanicAndBookingByUuid(slotUuid)
                             .orElseThrow(() -> {
                                 log.error("Slot with UUID: [{}] not found", slotUuid);
                                 return new ServiceException(E05001);
                             });
    }

    private void validateSlot(Slot slot) {
        validateActiveBooking(slot);
        validateSlotStatus(slot);
        validateSlotOwner(slot);
    }

    private void validateActiveBooking(Slot slot) {
        boolean activeBookingExists = slot.getBookings()
                                          .stream()
                                          .anyMatch(b -> b.getStatus() == BookingStatus.CONFIRMED);

        if (activeBookingExists) {
            throw new ServiceException(E05004);
        }
    }

    private void validateSlotStatus(Slot slot) {
        if (SlotStatus.AVAILABLE != slot.getStatus()) {
            throw new ServiceException(E05004);
        }
    }

    private void validateSlotOwner(Slot slot) {
        if (slot.getMechanic().getUuid().equals(getLoggedUserUUID())) {
            throw new ServiceException(E06004);
        }
    }

    private static Booking buildBooking(Slot slot) {
        return Booking.builder()
                      .uuid(randomUUID())
                      .status(BookingStatus.CONFIRMED)
                      .slot(slot)
                      .client(getLoggedUser())
                      .build();
    }

    private Booking getBookingWithClientAndSlotOrElseThrow(UUID bookingUuid) {
        return bookingRepository.findBookingWithClientAndSlotByUuid(bookingUuid)
                                .orElseThrow(() -> {
                                    log.error("Booking with UUID: [{}] not found", bookingUuid);
                                    return new ServiceException(E06000);
                                });
    }

    private void validateBooking(Booking booking) {
        validateBookingOwner(SecurityUtils.getLoggedUserUUID(), booking.getClient().getUuid(), booking.getUuid());
        validateBookingStatus(booking);
    }

    private void validateBookingOwner(UUID loggedUserUuid, UUID bookingOwnerUuid, UUID bookingUuid) {
        if (!loggedUserUuid.equals(bookingOwnerUuid)) {
            log.error("Logged in user is not the owner of the booking. Booking UUID: [{}], Logged user UUID: [{}], Booking owner UUID: [{}]", bookingUuid, loggedUserUuid, bookingOwnerUuid);
            throw new ServiceException(E06003);
        }
    }

    private void validateBookingStatus(Booking booking) {
        if (BookingStatus.CONFIRMED != booking.getStatus()) {
            throw new ServiceException(E06001);
        }
    }
}