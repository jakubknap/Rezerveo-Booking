package pl.rezerveo.booking.booking.service

import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import pl.rezerveo.booking.booking.enumerated.BookingStatus
import pl.rezerveo.booking.booking.model.Booking
import pl.rezerveo.booking.booking.repository.BookingRepository
import pl.rezerveo.booking.booking.service.impl.BookingServiceImpl
import pl.rezerveo.booking.exception.exception.ServiceException
import pl.rezerveo.booking.slot.enumerate.SlotStatus
import pl.rezerveo.booking.slot.model.Slot
import pl.rezerveo.booking.slot.repository.SlotRepository
import pl.rezerveo.booking.user.model.User
import spock.lang.Specification

import static java.util.UUID.randomUUID
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E05001
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E05004
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E06000
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E06001
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E06003
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E06004
import static pl.rezerveo.booking.common.enumerated.ResponseCode.S00000
import static pl.rezerveo.booking.common.enumerated.ResponseCode.S00001
import static pl.rezerveo.booking.slot.enumerate.SlotStatus.BOOKED
import static pl.rezerveo.booking.slot.enumerate.SlotStatus.CANCELED

class BookingServiceTest extends Specification {

    BookingRepository bookingRepository = Mock()
    SlotRepository slotRepository = Mock()

    BookingService bookingService = new BookingServiceImpl(slotRepository, bookingRepository)

    def user = new User(uuid: randomUUID(), email: "user@example.com", password: "encoded-pass")

    def setup() {
        def authentication = new TestingAuthenticationToken(user, null)
        authentication.setAuthenticated(true)
        SecurityContextHolder.getContext().setAuthentication(authentication)
    }

    def cleanup() {
        SecurityContextHolder.clearContext()
    }

    def "bookSlot should successfully book available slot"() {
        given:
        def slot = new Slot(uuid: randomUUID(), status: SlotStatus.AVAILABLE, bookings: [], mechanic: new User(uuid: randomUUID()))
        slotRepository.findSlotWithMechanicAndBookingByUuid(_ as UUID) >> Optional.of(slot)

        when:
        def response = bookingService.bookSlot(slot.uuid)

        then:
        1 * bookingRepository.save(_)
        1 * slotRepository.save(slot)
        response.status == S00000
        slot.status == BOOKED
    }

    def "bookSlot should throw exception if slot not found"() {
        given:
        slotRepository.findSlotWithMechanicAndBookingByUuid(_ as UUID) >> Optional.empty()

        when:
        bookingService.bookSlot(randomUUID())

        then:
        0 * bookingRepository.save(_)
        0 * slotRepository.save(_)
        def ex = thrown(ServiceException)
        ex.status == E05001
    }

    def "bookSlot should throw exception if slot already booked"() {
        given:
        def slot = new Slot(uuid: randomUUID(), status: BOOKED, bookings: [], mechanic: new User(uuid: randomUUID()))
        slotRepository.findSlotWithMechanicAndBookingByUuid(_ as UUID) >> Optional.of(slot)

        when:
        bookingService.bookSlot(slot.uuid)

        then:
        0 * bookingRepository.save(_)
        0 * slotRepository.save(_)
        def ex = thrown(ServiceException)
        ex.status == E05004
    }

    def "bookSlot should throw exception if user tries to book own slot"() {
        given:
        def slot = new Slot(uuid: randomUUID(), status: SlotStatus.AVAILABLE, bookings: [], mechanic: user)
        slotRepository.findSlotWithMechanicAndBookingByUuid(_ as UUID) >> Optional.of(slot)

        when:
        bookingService.bookSlot(slot.uuid)

        then:
        0 * bookingRepository.save(_)
        0 * slotRepository.save(_)
        def ex = thrown(ServiceException)
        ex.status == E06004
    }

    def "cancelBooking should successfully cancel booking and update slot status"() {
        given:
        def slot = new Slot(uuid: randomUUID(), status: BOOKED, mechanic: new User(uuid: randomUUID()))
        def booking = new Booking(uuid: randomUUID(), status: BookingStatus.CONFIRMED, slot: slot, client: user)
        slot.bookings << booking
        bookingRepository.findBookingWithClientAndSlotByUuid(_ as UUID) >> Optional.of(booking)

        when:
        def response = bookingService.cancelBooking(booking.uuid)

        then:
        1 * bookingRepository.save(_)
        1 * slotRepository.save(_)
        response.status == S00001
        booking.status == BookingStatus.CANCELED
        slot.status == SlotStatus.AVAILABLE
    }

    def "cancelBooking should throw exception if booking not found"() {
        given:
        bookingRepository.findBookingWithClientAndSlotByUuid(_ as UUID) >> Optional.empty()

        when:
        bookingService.cancelBooking(randomUUID())

        then:
        0 * bookingRepository.save(_)
        0 * slotRepository.save(_)
        def ex = thrown(ServiceException)
        ex.status == E06000
    }

    def "cancelBooking should throw exception if user not owner"() {
        given:
        def slot = new Slot(uuid: randomUUID(), status: BOOKED, mechanic: new User(uuid: randomUUID()))
        def booking = new Booking(uuid: randomUUID(), status: BookingStatus.CONFIRMED, slot: slot, client: new User(uuid: randomUUID()))
        bookingRepository.findBookingWithClientAndSlotByUuid(_ as UUID) >> Optional.of(booking)

        when:
        bookingService.cancelBooking(booking.uuid)

        then:
        0 * bookingRepository.save(_)
        0 * slotRepository.save(_)
        def ex = thrown(ServiceException)
        ex.status == E06003
    }

    def "cancelBooking should throw exception if booking status not CONFIRMED"() {
        given:
        def slot = new Slot(uuid: randomUUID(), status: CANCELED, mechanic: new User(uuid: randomUUID()))
        def booking = new Booking(uuid: randomUUID(), status: BookingStatus.CANCELED, slot: slot, client: user)
        bookingRepository.findBookingWithClientAndSlotByUuid(_ as UUID) >> Optional.of(booking)

        when:
        bookingService.cancelBooking(booking.uuid)

        then:
        0 * bookingRepository.save(_)
        0 * slotRepository.save(_)
        def ex = thrown(ServiceException)
        ex.status == E06001
    }
}
