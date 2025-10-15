package pl.rezerveo.booking.slot.service.impl

import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import pl.rezerveo.booking.booking.enumerated.BookingStatus
import pl.rezerveo.booking.booking.model.Booking
import pl.rezerveo.booking.booking.repository.BookingRepository
import pl.rezerveo.booking.exception.exception.ServiceException
import pl.rezerveo.booking.slot.dto.request.CreateSlotRequest
import pl.rezerveo.booking.slot.model.Slot
import pl.rezerveo.booking.slot.repository.SlotRepository
import pl.rezerveo.booking.slot.service.SlotService
import pl.rezerveo.booking.user.model.User
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalTime

import static java.util.UUID.randomUUID
import static pl.rezerveo.booking.booking.enumerated.BookingStatus.CONFIRMED
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E05000
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E05001
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E05002
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E05003
import static pl.rezerveo.booking.common.enumerated.ResponseCode.S00001
import static pl.rezerveo.booking.common.enumerated.ResponseCode.S00003
import static pl.rezerveo.booking.slot.enumerate.ServiceType.OIL_CHANGE
import static pl.rezerveo.booking.slot.enumerate.SlotStatus.AVAILABLE
import static pl.rezerveo.booking.slot.enumerate.SlotStatus.BOOKED
import static pl.rezerveo.booking.slot.enumerate.SlotStatus.CANCELED

class SlotServiceImplTest extends Specification {

    SlotRepository slotRepository = Mock(SlotRepository)
    BookingRepository bookingRepository = Mock(BookingRepository)

    SlotService slotService = new SlotServiceImpl(slotRepository, bookingRepository)

    def user = new User(uuid: randomUUID(), email: "user@example.com", password: "encoded-pass")

    def setup() {
        def authentication = new TestingAuthenticationToken(user, null)
        authentication.setAuthenticated(true)
        SecurityContextHolder.getContext().setAuthentication(authentication)
    }

    def cleanup() {
        SecurityContextHolder.clearContext()
    }

    def "should create slot successfully"() {
        given:
        def request = new CreateSlotRequest(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 0), OIL_CHANGE)
        slotRepository.existsByMechanicAndDateAndTimeOverlap(_ as User, _ as LocalDate, _ as LocalTime, _ as LocalTime) >> false

        when:
        def response = slotService.createSlot(request)

        then:
        1 * slotRepository.save(_)
        response.status == S00003
    }

    def "should fail to create overlapping slot"() {
        given:
        def request = new CreateSlotRequest(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 0), OIL_CHANGE)
        slotRepository.existsByMechanicAndDateAndTimeOverlap(_ as User, _ as LocalDate, _ as LocalTime, _ as LocalTime) >> true

        when:
        slotService.createSlot(request)

        then:
        def ex = thrown(ServiceException)
        ex.status == E05000
    }

    def "should cancel slot successfully with booked bookings"() {
        given:
        def slotUuid = randomUUID()
        def slot = new Slot(uuid: slotUuid, status: BOOKED, mechanic: user)
        def booking = new Booking(uuid: randomUUID(), status: CONFIRMED, slot: slot)
        slot.setBookings(List.of(booking))

        slotRepository.findSlotWithMechanicAndBookingByUuid(slotUuid) >> Optional.of(slot)

        when:
        def response = slotService.cancelSlot(slotUuid)

        then:
        1 * slotRepository.save(_)
        1 * bookingRepository.saveAll(_)
        slot.status == CANCELED
        slot.bookings.get(0).status == BookingStatus.CANCELED
        response.status == S00001
    }

    def "should fail to cancel slot not found"() {
        given:
        def slotUuid = randomUUID()
        slotRepository.findSlotWithMechanicAndBookingByUuid(slotUuid) >> Optional.empty()

        when:
        slotService.cancelSlot(slotUuid)

        then:
        0 * slotRepository.save(_)
        0 * bookingRepository.save(_)
        def ex = thrown(ServiceException)
        ex.status == E05001
    }

    def "should fail to cancel slot if user is not owner"() {
        given:
        def slotUuid = randomUUID()
        def slot = new Slot(uuid: slotUuid, status: AVAILABLE, mechanic: new User(uuid: randomUUID()))

        slotRepository.findSlotWithMechanicAndBookingByUuid(slotUuid) >> Optional.of(slot)

        when:
        slotService.cancelSlot(slotUuid)

        then:
        0 * slotRepository.save(_)
        0 * bookingRepository.save(_)
        def ex = thrown(ServiceException)
        ex.status == E05002
    }

    def "should fail to cancel already canceled slot"() {
        given:
        def slotUuid = randomUUID()
        def slot = new Slot(uuid: slotUuid, status: CANCELED, mechanic: user)

        slotRepository.findSlotWithMechanicAndBookingByUuid(slotUuid) >> Optional.of(slot)

        when:
        slotService.cancelSlot(slotUuid)

        then:
        0 * slotRepository.save(_)
        0 * bookingRepository.save(_)
        def ex = thrown(ServiceException)
        ex.status == E05003
    }
}