package pl.rezerveo.booking.authentication.service

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import pl.rezerveo.booking.authentication.dto.request.LoginRequest
import pl.rezerveo.booking.authentication.dto.request.RegisterRequest
import pl.rezerveo.booking.authentication.service.impl.AuthenticationServiceImpl
import pl.rezerveo.booking.exception.dto.response.BaseResponse
import pl.rezerveo.booking.exception.exception.ServiceException
import pl.rezerveo.booking.security.JwtService
import pl.rezerveo.booking.security.encryption.EncryptionService
import pl.rezerveo.booking.token.model.Token
import pl.rezerveo.booking.token.repository.TokenRepository
import pl.rezerveo.booking.token.service.TokenService
import pl.rezerveo.booking.token.service.TokenServiceFactory
import pl.rezerveo.booking.user.mapper.UserMapper
import pl.rezerveo.booking.user.model.User
import pl.rezerveo.booking.user.repository.UserRepository
import pl.rezerveo.booking.user.service.UserService
import spock.lang.Specification

import static java.util.UUID.randomUUID
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E03000
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E03002
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E03003
import static pl.rezerveo.booking.common.enumerated.ResponseCode.S00000
import static pl.rezerveo.booking.common.enumerated.ResponseCode.S00003
import static pl.rezerveo.booking.token.enumerated.TokenType.ACCESS_TOKEN
import static pl.rezerveo.booking.token.enumerated.TokenType.ACCOUNT_ACTIVATION_TOKEN
import static pl.rezerveo.booking.token.enumerated.TokenType.REFRESH_TOKEN
import static pl.rezerveo.booking.user.enumerated.Role.CLIENT
import static pl.rezerveo.booking.user.enumerated.UserStatus.ACTIVE
import static pl.rezerveo.booking.user.enumerated.UserStatus.REGISTERED

class AuthenticationServiceTest extends Specification {

    def userRepository = Mock(UserRepository)
    def userMapper = Mock(UserMapper)
    def userService = Mock(UserService)
    def jwtService = Mock(JwtService)
    def tokenRepository = Mock(TokenRepository)
    def authenticationManager = Mock(AuthenticationManager)
    def tokenServiceFactory = Mock(TokenServiceFactory)
    def encryptionService = Mock(EncryptionService)

    def authService = new AuthenticationServiceImpl(userRepository, userMapper, userService, jwtService, tokenRepository, authenticationManager,
            tokenServiceFactory, encryptionService)

    def "register should throw exception if user exists"() {
        given:
        def request = new RegisterRequest("firstName", "lastName", "test@example.com", "123456789", "pass", CLIENT)
        encryptionService.encrypt(_ as String) >> "asd2312"
        userRepository.existsByEmail(_ as String) >> true

        when:
        authService.register(request)

        then:
        def ex = thrown(ServiceException)
        ex.getStatus() == E03000
    }

    def "register should save new user"() {
        given:
        def request = new RegisterRequest("firstName", "lastName", "test@example.com", "123456789", "pass", CLIENT)
        def user = new User(uuid: randomUUID(), email: request.email())
        userRepository.existsByEmail(_ as String) >> false
        userMapper.mapToUserEntity(request) >> user
        def tokenService = Mock(TokenService)
        tokenServiceFactory.getTokenService(ACCOUNT_ACTIVATION_TOKEN) >> tokenService

        when:
        def response = authService.register(request)

        then:
        1 * userRepository.save(user)
        response instanceof BaseResponse
        response.status == S00003
    }

    def "activateAccount should activate user if status REGISTERED"() {
        given:
        def user = new User(uuid: randomUUID(), status: REGISTERED)
        def token = new Token(token: randomUUID(), user: user)
        def tokenService = Mock(TokenService)
        tokenServiceFactory.getTokenService(ACCOUNT_ACTIVATION_TOKEN) >> tokenService
        tokenService.getAndValidateToken(token.token) >> token

        when:
        def response = authService.activateAccount(UUID.fromString(token.token))

        then:
        1 * userRepository.save(user)
        1 * tokenRepository.save(token)
        user.status == ACTIVE
        token.usedAt != null
        response.status == S00000
    }

    def "activateAccount should throw exception if user already ACTIVE"() {
        given:
        def user = new User(uuid: randomUUID(), status: ACTIVE)
        def token = new Token(token: randomUUID(), user: user)
        def tokenService = Mock(TokenService)
        tokenServiceFactory.getTokenService(ACCOUNT_ACTIVATION_TOKEN) >> tokenService
        tokenService.getAndValidateToken(token.token) >> token

        when:
        authService.activateAccount(UUID.fromString(token.token))

        then:
        def ex = thrown(ServiceException)
        ex.getStatus() == E03003
    }

    def "authenticate should throw exception if user not active"() {
        given:
        def user = new User(uuid: randomUUID(), email: "test@example.com", status: REGISTERED)
        def request = new LoginRequest(user.email, "pass")
        userService.getUserByEmail(user.email) >> user

        when:
        authService.authenticate(request)

        then:
        def ex = thrown(ServiceException)
        ex.getStatus() == E03002
    }

    def "authenticate should generate tokens for active user"() {
        given:
        def user = new User(uuid: randomUUID(), email: "test@example.com", status: ACTIVE)
        def request = new LoginRequest(user.email, "pass")
        userService.getUserByEmail(user.email) >> user
        authenticationManager.authenticate(_ as Authentication) >> { return null }
        jwtService.revokeAllUserJwtTokens(user.uuid) >> null

        def accessToken = new Token(token: randomUUID().toString(), tokenType: "ACCESS_TOKEN")
        def refreshToken = new Token(token: randomUUID().toString(), tokenType: "REFRESH_TOKEN")
        jwtService.generateAccessToken(user.email) >> "access"
        jwtService.generateRefreshToken(user.email) >> "refresh"
        jwtService.buildJwtTokenEntity(user, "access", ACCESS_TOKEN) >> accessToken
        jwtService.buildJwtTokenEntity(user, "refresh", REFRESH_TOKEN) >> refreshToken

        when:
        def response = authService.authenticate(request)

        then:
        1 * tokenRepository.saveAll([accessToken, refreshToken])
        response.accessToken() == accessToken.getToken()
        response.refreshToken() == refreshToken.getToken()
    }

    def "refreshToken should generate new access token"() {
        given:
        def user = new User(uuid: randomUUID(), email: "test@example.com")
        def refreshTokenStr = "refreshToken"
        def request = Mock(HttpServletRequest)
        request.getHeader("Authorization") >> "Bearer ${refreshTokenStr}"
        jwtService.readTokenFromHeader(request) >> refreshTokenStr
        jwtService.extractUsernameFromToken(refreshTokenStr) >> user.email
        userService.getUserByEmail(user.email) >> user

        def accessToken = new Token(token: randomUUID(), tokenType: "ACCESS_TOKEN")
        jwtService.generateAccessToken(user.email) >> "access"
        jwtService.buildJwtTokenEntity(user, "access", ACCESS_TOKEN) >> accessToken

        when:
        def response = authService.refreshToken(request)

        then:
        1 * tokenRepository.save(accessToken)
        response.accessToken() == accessToken.getToken()
        response.refreshToken() == refreshTokenStr
    }
}