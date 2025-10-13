package pl.rezerveo.booking.token.model;

import jakarta.persistence.*;
import lombok.*;
import pl.rezerveo.booking.common.entity.auditable.DateAuditEntity;
import pl.rezerveo.booking.token.enumerated.TokenType;
import pl.rezerveo.booking.user.model.User;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token extends DateAuditEntity {

    @Column(nullable = false, unique = true)
    private String token;

    @Enumerated(STRING)
    @Column(nullable = false)
    private TokenType tokenType;

    @Column(nullable = false)
    private boolean revoked;

    // The following 2 fields apply only to account management tokens, not to token authorization - JWT
    @Column(updatable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime usedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}