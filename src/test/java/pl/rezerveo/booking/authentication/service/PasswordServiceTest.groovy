package pl.rezerveo.booking.authentication.service

import org.springframework.security.crypto.password.PasswordEncoder
import pl.rezerveo.booking.authentication.dto.request.ConfirmResetPasswordRequest
import pl.rezerveo.booking.authentication.dto.request.ResetPasswordRequest
import pl.rezerveo.booking.authentication.service.impl.PasswordServiceImpl
import pl.rezerveo.booking.exception.dto.response.BaseResponse
import pl.rezerveo.booking.exception.exception.CustomValidationException
import pl.rezerveo.booking.exception.exception.ServiceException
import pl.rezerveo.booking.token.model.Token
import pl.rezerveo.booking.token.repository.TokenRepository
import pl.rezerveo.booking.token.service.TokenService
import pl.rezerveo.booking.token.service.TokenServiceFactory
import pl.rezerveo.booking.user.model.User
import pl.rezerveo.booking.user.repository.UserRepository
import pl.rezerveo.booking.user.service.UserService
import spock.lang.Specification

import static pl.rezerveo.booking.common.enumerated.ResponseCode.E00000
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E03001
import static pl.rezerveo.booking.common.enumerated.ResponseCode.S00000
import static pl.rezerveo.booking.token.enumerated.TokenType.PASSWORD_RESET_TOKEN

class PasswordServiceTest extends Specification {

    def userService = Mock(UserService)
    def tokenServiceFactory = Mock(TokenServiceFactory)
    def tokenRepository = Mock(TokenRepository)
    def passwordEncoder = Mock(PasswordEncoder)
    def userRepository = Mock(UserRepository)

    def passwordService = new PasswordServiceImpl(userService, tokenServiceFactory, tokenRepository, passwordEncoder, userRepository)

    def "resetPasswordRequest should return generic response if user not found"() {
        given:
        def email = "notfound@example.com"
        userService.getUserByEmail(email) >> { throw new ServiceException(E03001) }

        when:
        def response = passwordService.resetPasswordRequest(new ResetPasswordRequest(email))

        then:
        response instanceof BaseResponse
        response.getStatus() == S00000
    }

    def "confirmResetPassword should update password if valid"() {
        given:
        def user = new User(uuid: UUID.randomUUID(), password: "encodedOld")
        def token = new Token(token: UUID.randomUUID(), user: user)
        def tokenService = Mock(TokenService)
        def request = new ConfirmResetPasswordRequest(UUID.fromString(token.token), "newPass", "newPass")

        tokenServiceFactory.getTokenService(PASSWORD_RESET_TOKEN) >> tokenService
        tokenService.getAndValidateToken(token.token.toString()) >> token
        passwordEncoder.matches("newPass", "encodedOld") >> false
        passwordEncoder.encode("newPass") >> "encodedNew"

        when:
        def response = passwordService.confirmResetPassword(request)

        then:
        1 * userRepository.save(user)
        1 * tokenRepository.save(token)
        response instanceof BaseResponse
        user.password == "encodedNew"
        token.usedAt != null
    }

    def "confirmResetPassword should throw exception if passwords do not match"() {
        given:
        def user = new User(uuid: UUID.randomUUID(), password: "encodedOld")
        def token = new Token(token: UUID.randomUUID(), user: user)
        def tokenService = Mock(TokenService)
        def request = new ConfirmResetPasswordRequest(UUID.fromString(token.token), "pass1", "pass2")

        tokenServiceFactory.getTokenService(PASSWORD_RESET_TOKEN) >> tokenService
        tokenService.getAndValidateToken(token.token.toString()) >> token

        when:
        passwordService.confirmResetPassword(request)

        then:
        def ex = thrown(CustomValidationException)
        ex.getStatus() == E00000
        0 * userRepository.save(_)
    }

    def "confirmResetPassword should throw exception if new password is same as old"() {
        given:
        def user = new User(uuid: UUID.randomUUID(), password: "encodedOld")
        def token = new Token(token: UUID.randomUUID(), user: user)
        def tokenService = Mock(TokenService)
        def request = new ConfirmResetPasswordRequest(UUID.fromString(token.token), "samePass", "samePass")

        tokenServiceFactory.getTokenService(PASSWORD_RESET_TOKEN) >> tokenService
        tokenService.getAndValidateToken(token.token.toString()) >> token
        passwordEncoder.matches("samePass", "encodedOld") >> true

        when:
        passwordService.confirmResetPassword(request)

        then:
        def ex = thrown(CustomValidationException)
        ex.getStatus() == E00000
        0 * userRepository.save(_)
    }
}