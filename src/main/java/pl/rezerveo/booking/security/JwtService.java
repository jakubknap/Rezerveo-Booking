package pl.rezerveo.booking.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import pl.rezerveo.booking.exception.exception.ServiceException;
import pl.rezerveo.booking.properties.JwtProperties;
import pl.rezerveo.booking.token.dto.TokenStatusAndType;
import pl.rezerveo.booking.token.enumerated.TokenType;
import pl.rezerveo.booking.token.model.Token;
import pl.rezerveo.booking.token.repository.TokenRepository;
import pl.rezerveo.booking.user.model.User;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.*;
import static pl.rezerveo.booking.token.enumerated.TokenType.ACCESS_TOKEN;
import static pl.rezerveo.booking.token.enumerated.TokenType.REFRESH_TOKEN;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String BEARER = "Bearer ";
    private static final long OFFSET_NOW = 0L;

    private final JwtProperties jwtProperties;
    private final TokenRepository tokenRepository;

    public String readTokenFromHeader(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isNull(authHeader) || !authHeader.startsWith(BEARER)) {
            return null;
        }
        return authHeader.substring(BEARER.length());
    }

    public String extractUsernameFromToken(String token) {
        final String username = extractClaim(token, Claims::getSubject);

        if (isNull(username)) {
            log.error("Not found username in token");
            throw new ServiceException(E01002);
        }

        return username;
    }

    public void validateToken(String token, String userEmailFromUserDetails, TokenType expectedTokenType) {
        validateUsernameFromToken(token, userEmailFromUserDetails);
        validateTokenExpiration(token);
        validateTokenStatusAndExpectedType(token, expectedTokenType);
    }

    public void revokeAllUserJwtTokens(UUID userUuid) {
        log.info("Revoking all valid JWT tokens for user with UUID: [{}]", userUuid);
        tokenRepository.revokeAllValidTokensByUserUuidAndTokenTypes(userUuid, List.of(ACCESS_TOKEN, REFRESH_TOKEN));
    }

    public void revokeAllUserJwtAccessTokens(UUID userUuid) {
        log.info("Revoking all valid JWT {}S for user with UUID: [{}]", ACCESS_TOKEN, userUuid);
        tokenRepository.revokeAllValidTokensByUserUuidAndTokenTypes(userUuid, List.of(ACCESS_TOKEN));
    }

    public String generateAccessToken(String userEmail) {
        return generateToken(new HashMap<>(), userEmail, jwtProperties.getAccessTokenExpiration());
    }

    public String generateRefreshToken(String userEmail) {
        return generateToken(new HashMap<>(), userEmail, jwtProperties.getRefreshTokenExpiration());
    }

    public Token buildJwtTokenEntity(User user, String token, TokenType tokenType) {
        return Token.builder()
                .token(token)
                .tokenType(tokenType)
                .revoked(false)
                .user(user)
                .build();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        if (isNull(token)) {
            log.error("Missing token or invalid header format (missing Bearer clause)");
            throw new ServiceException(E01000);
        }

        Claims claims;

        try {
            claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException ex) {
            log.error("Expired token: {}", ex.getMessage(), ex);
            throw new ServiceException(E01001);

        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException ex) {
            log.error("Invalid token: {}", ex.getMessage(), ex);
            throw new ServiceException(E01002);

        } catch (Exception ex) {
            log.error("Error while parsing token: {}", ex.getMessage(), ex);
            throw new ServiceException(E00006);
        }

        if (isNull(claims)) {
            log.error("No claims found in token");
            throw new ServiceException(E01002);
        }

        return claims;
    }

    private void validateUsernameFromToken(String token, String userEmailFromUserDetails) {
        final String username = extractUsernameFromToken(token);

        if (!username.equals(userEmailFromUserDetails)) {
            log.error("Token is registered for different user");
            throw new ServiceException(E01002);
        }
    }

    private void validateTokenExpiration(String token) {
        boolean isTokenExpired = extractExpiration(token).before(getDate(OFFSET_NOW));

        if (isTokenExpired) {
            log.error("Token expired");
            throw new ServiceException(E01001);
        }
    }

    private Date extractExpiration(String token) {
        final Date expirationDate = extractClaim(token, Claims::getExpiration);

        if (isNull(expirationDate)) {
            log.error("Expiration date not found in token");
            throw new ServiceException(E01002);
        }

        return expirationDate;
    }

    private void validateTokenStatusAndExpectedType(String token, TokenType expectedType) {
        final TokenStatusAndType tokenStatusAndType = getTokenStatusAndTypeOrElseThrow(token);

        if (tokenStatusAndType.isRevoked()) {
            log.error("Token is revoked");
            throw new ServiceException(E01002);
        }

        final TokenType tokenType = tokenStatusAndType.tokenType();
        if (expectedType != tokenType) {
            log.error("Token type is invalid for this operation. Expected token type is: [{}], provided: [{}]", expectedType, tokenType);
            throw new ServiceException(E01002);
        }
    }

    private TokenStatusAndType getTokenStatusAndTypeOrElseThrow(String token) {
        return tokenRepository.getTokenStatusAndType(token)
                .orElseThrow(() -> {
                    log.error("Token not found");
                    return new ServiceException(E01003);
                });
    }

    private String generateToken(Map<String, Object> extraClaims, String userEmail, Duration expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userEmail)
                .issuedAt(getDate(OFFSET_NOW))
                .expiration(getDate(expiration.toMillis()))
                .signWith(getSigningKey())
                .compact();
    }

    private Date getDate(long offset) {
        return new Date(System.currentTimeMillis() + offset);
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecretKey()));
    }
}