package pl.rezerveo.booking.slot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rezerveo.booking.booking.enumerated.BookingStatus;
import pl.rezerveo.booking.booking.model.Booking;
import pl.rezerveo.booking.booking.repository.BookingRepository;
import pl.rezerveo.booking.common.dto.PageResponse;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;
import pl.rezerveo.booking.exception.exception.ServiceException;
import pl.rezerveo.booking.slot.dto.request.CreateSlotRequest;
import pl.rezerveo.booking.slot.dto.response.MechanicSlotsResponse;
import pl.rezerveo.booking.slot.enumerate.SlotStatus;
import pl.rezerveo.booking.slot.model.Slot;
import pl.rezerveo.booking.slot.repository.SlotRepository;
import pl.rezerveo.booking.slot.service.SlotService;
import pl.rezerveo.booking.user.model.User;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E05000;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E05001;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E05002;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E05003;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.S00001;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.S00003;
import static pl.rezerveo.booking.security.util.SecurityUtils.getLoggedUser;
import static pl.rezerveo.booking.security.util.SecurityUtils.getLoggedUserUUID;
import static pl.rezerveo.booking.slot.enumerate.SlotStatus.AVAILABLE;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService {

    private final SlotRepository slotRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BaseResponse createSlot(CreateSlotRequest request) {
        User loggedUser = getLoggedUser();

        validateOverlapping(request, loggedUser);

        Slot slot = buildSlot(request, loggedUser);

        slotRepository.save(slot);

        return new BaseResponse(S00003);
    }

    @Override
    public PageResponse<MechanicSlotsResponse> getMechanicSlots(Pageable pageable) {
        UUID userUuid = getLoggedUserUUID();

        Page<MechanicSlotsResponse> slots = slotRepository.getMechanicSlots(pageable, userUuid);

        return PageResponse.of(slots);
    }

    @Override
    @Transactional
    public BaseResponse cancelSlot(UUID slotUuid) {
        Slot slot = getSlotWithUserAndBookingOrElseThrow(slotUuid);

        validateSlotMechanic(getLoggedUserUUID(),
                             slot.getMechanic().getUuid(),
                             slot.getUuid());

        if (SlotStatus.CANCELED == slot.getStatus()) {
            return new BaseResponse(E05003);
        }

        if (SlotStatus.RESERVED == slot.getStatus()) {
            Booking booking = slot.getBooking();
            booking.setStatus(BookingStatus.CANCELED);
            bookingRepository.save(booking);
            //TOOD wysyłka powiadomienia
        }

        slot.setStatus(SlotStatus.CANCELED);
        slotRepository.save(slot);

        return new BaseResponse(S00001);
    }

    private void validateOverlapping(CreateSlotRequest request, User loggedUser) {
        boolean overlapping = slotRepository.existsByMechanicAndDateAndTimeOverlap(loggedUser, request.date(), request.startTime(), request.endTime());
        if (overlapping) {
            throw new ServiceException(E05000);
        }
    }

    private Slot buildSlot(CreateSlotRequest request, User loggedUser) {
        return Slot.builder()
                   .uuid(randomUUID())
                   .date(request.date())
                   .startTime(request.startTime())
                   .endTime(request.endTime())
                   .serviceType(request.serviceType())
                   .status(AVAILABLE)
                   .mechanic(loggedUser)
                   .build();
    }

    private Slot getSlotWithUserAndBookingOrElseThrow(UUID slotUuid) {
        return slotRepository.findSlotWithMechanicAndBookingByUuid(slotUuid)
                             .orElseThrow(() -> {
                                 log.error("Slot with UUID: [{}] not found", slotUuid);
                                 return new ServiceException(E05001);
                             });
    }

    private void validateSlotMechanic(UUID loggedUserUuid, UUID slotMechanicUuid, UUID slotUuid) {
        if (!loggedUserUuid.equals(slotMechanicUuid)) {
            log.error("Logged in user is not the owner of the slot. Slot UUID: [{}], Logged user UUID: [{}], Slot owner UUID: [{}]", slotUuid, loggedUserUuid, slotMechanicUuid);
            throw new ServiceException(E05002);
        }
    }
}