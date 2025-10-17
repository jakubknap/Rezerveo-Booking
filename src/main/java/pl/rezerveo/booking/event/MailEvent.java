package pl.rezerveo.booking.event;

import pl.rezerveo.booking.security.encryption.EncryptionService;

public record MailEvent(String userEmail, String userFirstName, String userLastName, String token, MailType mailType) {

    public MailEvent(String userEmail, String userFirstName, String userLastName, String token, MailType mailType, EncryptionService encryptionService) {
        this(encryptionService.encrypt(userEmail), encryptionService.encrypt(userFirstName), encryptionService.encrypt(userLastName), token, mailType);
    }
}