package pl.rezerveo.booking.token.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pl.rezerveo.booking.token.dto.TokenStatusAndType;
import pl.rezerveo.booking.token.enumerated.TokenType;
import pl.rezerveo.booking.token.model.Token;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Modifying
    @Transactional
    @Query("""
            UPDATE Token t
            SET t.revoked = TRUE
            WHERE t.revoked = FALSE
              AND t.user.uuid = :userUuid
              AND t.tokenType IN :tokenTypes
            """)
    void revokeAllValidTokensByUserUuidAndTokenTypes(UUID userUuid, List<TokenType> tokenTypes);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Token t
            SET t.revoked = TRUE
            WHERE t.revoked = FALSE
              AND t.user.uuid = :userUuid
            """)
    void revokeAllValidTokensByUserUuid(UUID userUuid);

    @Query("""
            SELECT new pl.rezerveo.booking.token.dto.TokenStatusAndType(t.revoked, t.tokenType)
            FROM Token t
            WHERE t.token = :token
            """)
    Optional<TokenStatusAndType> getTokenStatusAndType(String token);

    @Query("""
            SELECT t.user.uuid
            FROM Token t
            WHERE t.token = :token
            """)
    Optional<UUID> findUserUuidByToken(String token);

    @Query("""
            SELECT t
            FROM Token t
                     JOIN FETCH t.user
            WHERE t.token = :token
            """)
    Optional<Token> findTokenWithUserByToken(String token);
}