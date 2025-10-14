package pl.rezerveo.booking.common.enumerated;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    // GLOBAL SUCCESSES
    S00000("Success", OK),
    S00001("Success", NO_CONTENT),
    S00002("Success", ACCEPTED),
    S00003("Success", CREATED),

    // GLOBAL ERRORS
    E00000("Bad Request", BAD_REQUEST),
    E00001("Forbidden", FORBIDDEN),
    E00002("Not found", NOT_FOUND),
    E00003("Unauthorized", UNAUTHORIZED),
    E00004("Invalid request format", BAD_REQUEST),
    E00005("Invalid request", BAD_REQUEST),
    E00006("Internal server error", INTERNAL_SERVER_ERROR),

    // JWT TOKEN ERRORS
    E01000("Missing token", UNAUTHORIZED),
    E01001("Expired token", UNAUTHORIZED),
    E01002("Invalid token", UNAUTHORIZED),
    E01003("Token not found", UNAUTHORIZED),

    // AUTHENTICATION ERRORS
    E02000("Incorrect e-mail or password", UNAUTHORIZED),
    E02001("Account is inactive", UNAUTHORIZED),
    E02002("Account is blocked", UNAUTHORIZED),
    E02003("Account expired", UNAUTHORIZED),
    E02004("Account password expired", UNAUTHORIZED),

    // USER ERRORS
    E03000("User already exists", CONFLICT),
    E03001("User not found", NOT_FOUND),
    E03002("User does not have an active account", UNPROCESSABLE_ENTITY),
    E03003("User already had the account activated", UNPROCESSABLE_ENTITY),
    E03006("E-mail is taken by another user", UNPROCESSABLE_ENTITY),

    // TOKEN ERRORS
    E04000("Token not found", NOT_FOUND),
    E04001("Expired token", BAD_REQUEST),
    E04002("Revoked token", BAD_REQUEST),
    E04003("Invalid token", BAD_REQUEST),
    E04004("Token used", BAD_REQUEST),

    // SLOT ERRORS
    E05000("Selected time slot overlaps with an existing slot", CONFLICT),
    E05001("Slot not found", NOT_FOUND),
    E05002("Logged in user is not the owner of the slot", FORBIDDEN),
    E05003("Slot had already been canceled", UNPROCESSABLE_ENTITY);

    private final String message;
    private final HttpStatus httpStatus;
}