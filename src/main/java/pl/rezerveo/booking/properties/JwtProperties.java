package pl.rezerveo.booking.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtProperties {

    @NotBlank
    private String secretKey;

    @NotNull
    private Duration accessTokenExpiration;

    @NotNull
    private Duration refreshTokenExpiration;
}