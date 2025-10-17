package pl.rezerveo.booking.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rezerveo.booking.booking.dto.response.AvailableSlotsResponse;
import pl.rezerveo.booking.booking.dto.response.BookingListResponse;
import pl.rezerveo.booking.booking.dto.response.MechanicBookingListResponse;
import pl.rezerveo.booking.booking.enumerated.BookingStatus;
import pl.rezerveo.booking.booking.model.Booking;
import pl.rezerveo.booking.booking.repository.BookingRepository;
import pl.rezerveo.booking.booking.service.BookingService;
import pl.rezerveo.booking.common.dto.PageResponse;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;
import pl.rezerveo.booking.exception.exception.ServiceException;
import pl.rezerveo.booking.notification.NotificationPublisher;
import pl.rezerveo.booking.security.util.SecurityUtils;
import pl.rezerveo.booking.slot.enumerate.SlotStatus;
import pl.rezerveo.booking.slot.model.Slot;
import pl.rezerveo.booking.slot.repository.SlotRepository;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E05001;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E05002;
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
    private final NotificationPublisher notificationPublisher;

    @Override
    public PageResponse<AvailableSlotsResponse> getAvailableSlots(Pageable pageable) {
        log.info("Fetching available slots, pageable: {}", pageable);

        Page<AvailableSlotsResponse> availableSlots = slotRepository.findAvailableSlots(pageable);

        log.info("Found {} available slots", availableSlots.getTotalElements());
        return PageResponse.of(availableSlots);
    }

    @Override
    @Transactional
    public BaseResponse bookSlot(UUID slotUuid) {
        log.info("Attempting to book slot with UUID: {}", slotUuid);
        Slot slot = getSlotWithMechanicAndBookingOrElseThrow(slotUuid);

        validateSlot(slot);

        Booking booking = buildBooking(slot);

        slot.setStatus(SlotStatus.BOOKED);
        bookingRepository.save(booking);
        slotRepository.save(slot);

        notificationPublisher.notifyBookingConfirmedToMechanic(booking.getClient(), slot);
        notificationPublisher.notifyBookingConfirmedToClient(booking.getClient(), slot);

        log.info("Slot booked successfully, slot UUID: {}, booking UUID: {}", slot.getUuid(), booking.getUuid());
        return new BaseResponse(S00000);
    }

    @Override
    public PageResponse<BookingListResponse> getBookingList(Pageable pageable) {
        UUID loggedUserUuid = getLoggedUserUUID();
        log.info("Fetching booking list for user UUID: {}, pageable: {}", loggedUserUuid, pageable);

        Page<BookingListResponse> bookingList = bookingRepository.findAllByClientUuid(pageable, loggedUserUuid);

        log.info("Found {} bookings for user UUID: {}", bookingList.getTotalElements(), loggedUserUuid);
        return PageResponse.of(bookingList);
    }

    @Override
    @Transactional
    public BaseResponse cancelBooking(UUID bookingUuid) {
        log.info("Attempting to cancel booking with UUID: {}", bookingUuid);

        Booking booking = getBookingWithClientAndSlotOrElseThrow(bookingUuid);

        validateBooking(booking);

        booking.setStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);
        notificationPublisher.notifyBookingCanceledByClientToMechanic(booking.getClient(), booking.getSlot());

        Slot slot = booking.getSlot();

        boolean stillBooked = slot.getBookings()
                                  .stream()
                                  .anyMatch(b -> b.getStatus() == BookingStatus.CONFIRMED);

        if (!stillBooked) {
            slot.setStatus(SlotStatus.AVAILABLE);
            slotRepository.save(slot);
            log.info("Slot set to AVAILABLE as no other confirmed bookings exist, slot UUID: {}", slot.getUuid());
        }
        else {
            log.info("Slot remains BOOKED as there are other confirmed bookings, slot UUID: {}", slot.getUuid());
        }

        log.info("Booking canceled, booking UUID: {}", booking.getUuid());
        return new BaseResponse(S00001);
    }

    @Override
    @Transactional
    public BaseResponse cancelBookingByMechanic(UUID slotUuid, UUID bookingUuid) {
        log.info("Mechanic attempting to cancel booking [{}] in slot [{}]", bookingUuid, slotUuid);

        Slot slot = getSlotWithMechanicAndBookingOrElseThrow(slotUuid);

        validateSlotOwnerForMechanicCancel(slotUuid, slot);

        Booking booking = getBookingFromSlot(bookingUuid, slot);

        validateBookingStatus(booking);

        booking.setStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);
        log.info("Booking [{}] canceled by mechanic", bookingUuid);
        notificationPublisher.notifyBookingCanceledByMechanicToClient(booking.getClient(), slot);

        boolean stillBooked = slot.getBookings()
                                  .stream()
                                  .anyMatch(b -> b.getStatus() == BookingStatus.CONFIRMED);

        if (!stillBooked) {
            slot.setStatus(SlotStatus.AVAILABLE);
            slotRepository.save(slot);
            log.info("Slot set to AVAILABLE as no other confirmed bookings exist, slot UUID: {}", slot.getUuid());
        }
        else {
            log.info("Slot remains BOOKED as there are other confirmed bookings, slot UUID: {}", slot.getUuid());
        }

        return new BaseResponse(S00001);
    }

    @Override
    public PageResponse<MechanicBookingListResponse> getMechanicBookingHistory(Pageable pageable) {
        UUID mechanicUuid = getLoggedUserUUID();
        log.info("Fetching bookings for all slots of mechanic [{}]", mechanicUuid);

        Page<MechanicBookingListResponse> bookings = bookingRepository.findAllBySlotMechanicUuid(mechanicUuid, pageable);

        log.info("Found {} bookings for mechanic [{}]", bookings.getTotalElements(), mechanicUuid);
        return PageResponse.of(bookings);
    }

    private Slot getSlotWithMechanicAndBookingOrElseThrow(UUID slotUuid) {
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
            log.error("Cannot book slot UUID: {} because it has an active booking", slot.getUuid());
            throw new ServiceException(E05004);
        }
    }

    private void validateSlotStatus(Slot slot) {
        if (SlotStatus.AVAILABLE != slot.getStatus()) {
            log.error("Cannot book slot UUID: {} because status is {}", slot.getUuid(), slot.getStatus());
            throw new ServiceException(E05004);
        }
    }

    private void validateSlotOwner(Slot slot) {
        if (slot.getMechanic().getUuid().equals(getLoggedUserUUID())) {
            log.error("Logged-in user is the mechanic, cannot book own slot UUID: {}", slot.getUuid());
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
            log.error("Cannot cancel booking UUID: {} because status is {}", booking.getUuid(), booking.getStatus());
            throw new ServiceException(E06001);
        }
    }

    private void validateSlotOwnerForMechanicCancel(UUID slotUuid, Slot slot) {
        if (!slot.getMechanic().getUuid().equals(getLoggedUserUUID())) {
            log.error("Logged in user is not the owner of slot [{}]", slotUuid);
            throw new ServiceException(E05002);
        }
    }

    private Booking getBookingFromSlot(UUID bookingUuid, Slot slot) {
        return slot.getBookings()
                   .stream()
                   .filter(b -> b.getUuid().equals(bookingUuid))
                   .findFirst()
                   .orElseThrow(() -> {
                       log.error("Booking with UUID: [{}] not found", bookingUuid);
                       return new ServiceException(E06000);
                   });
    }
}