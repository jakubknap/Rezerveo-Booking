package pl.rezerveo.booking.event;

import pl.rezerveo.booking.security.encryption.EncryptionService;

public record BookingEvent(String targetEmail, String title, String message) {

    public BookingEvent(String targetEmail, String title, String message, EncryptionService encryptionService) {
        this(encryptionService.encrypt(targetEmail), title, encryptionService.encrypt(message));
    }
}