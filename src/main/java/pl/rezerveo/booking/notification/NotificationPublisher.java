package pl.rezerveo.booking.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rezerveo.booking.booking.model.Booking;
import pl.rezerveo.booking.event.BookingEvent;
import pl.rezerveo.booking.event.service.RabbitEventPublisher;
import pl.rezerveo.booking.slot.model.Slot;
import pl.rezerveo.booking.user.model.User;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RabbitEventPublisher eventPublisher;

    public void notifyBookingConfirmedToClient(User client, Slot slot) {
        BookingEvent event = new BookingEvent(client.getEmail(),
                                              "Rezerwacja potwierdzona",
                                              "Twoja rezerwacja na slot " + slot.getDate() + " od " + slot.getStartTime() + " do " + slot.getEndTime() +
                                              " została pomyślnie utworzona.");
        eventPublisher.sendBookingEvent(event);
    }

    public void notifyBookingConfirmedToMechanic(User client, Slot slot) {
        BookingEvent event = new BookingEvent(slot.getMechanic().getEmail(),
                                              "Nowa rezerwacja na Twój slot",
                                              "Klient " + client.getFirstName() + " " + client.getLastName() + " zarezerwował Twój slot " + slot.getDate() + " od " +
                                              slot.getStartTime() + " do " + slot.getEndTime() + ".");
        eventPublisher.sendBookingEvent(event);
    }

    public void notifyBookingCanceledByClientToMechanic(User client, Slot slot) {
        BookingEvent event = new BookingEvent(slot.getMechanic().getEmail(),
                                              "Rezerwacja anulowana przez klienta",
                                              "Klient " + client.getFirstName() + " " + client.getLastName() + " anulował swoją rezerwację na Twój slot " + slot.getDate() +
                                              " od " + slot.getStartTime() + " do " + slot.getEndTime() + ".");
        eventPublisher.sendBookingEvent(event);
    }

    public void notifyBookingCanceledByMechanicToClient(User client, Slot slot) {
        BookingEvent event = new BookingEvent(client.getEmail(),
                                              "Rezerwacja anulowana przez mechanika",
                                              "Twój slot " + slot.getDate() + " od " + slot.getStartTime() + " do " + slot.getEndTime() + " został anulowany przez mechanika " +
                                              slot.getMechanic().getFirstName() + " " + slot.getMechanic().getLastName() + ".");
        eventPublisher.sendBookingEvent(event);
    }

    public void notifySlotCanceledToClients(Booking booking) {
        BookingEvent event = new BookingEvent(booking.getClient().getEmail(),
                                              "Slot anulowany przez mechanika",
                                              "Mechanik " + booking.getSlot().getMechanic().getFirstName() + " " +
                                              booking.getSlot().getMechanic().getLastName() + " anulował slot dnia " + booking.getSlot().getDate() + " od " +
                                              booking.getSlot().getStartTime() + " do " + booking.getSlot().getEndTime() + ". Twoja rezerwacja została anulowana.");
        eventPublisher.sendBookingEvent(event);
    }
}