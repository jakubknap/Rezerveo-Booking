package pl.rezerveo.booking.slot.service;

import org.springframework.data.domain.Pageable;
import pl.rezerveo.booking.common.dto.PageResponse;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;
import pl.rezerveo.booking.slot.dto.request.CreateSlotRequest;
import pl.rezerveo.booking.slot.dto.response.MechanicSlotsResponse;

import java.util.UUID;

public interface SlotService {

    BaseResponse createSlot(CreateSlotRequest request);

    PageResponse<MechanicSlotsResponse> getMechanicSlots(Pageable pageable);

    BaseResponse cancelSlot(UUID slotUuid);
}