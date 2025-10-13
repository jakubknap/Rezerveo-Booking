package pl.rezerveo.booking.security.encryption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.rezerveo.booking.properties.EncryptionProperties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class EncryptionService {

    private static final String ALGORITHM = "AES";

    private final SecretKey secretKey;

    public EncryptionService(EncryptionProperties encryptionProperties) {
        byte[] keyBytes = Base64.getDecoder().decode(encryptionProperties.getKey());
        this.secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public String encrypt(String plainText) {
        if (isNull(plainText)) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            log.error("Exception occurred during data encryption: ", ex);
            throw new RuntimeException(ex);
        }
    }

    public String decrypt(String cipherText) {
        if (isNull(cipherText)) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.error("Exception occurred during data decrypting: ", ex);
            throw new RuntimeException(ex);
        }
    }
}