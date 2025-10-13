package pl.rezerveo.booking.user.service

import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import pl.rezerveo.booking.exception.exception.CustomValidationException
import pl.rezerveo.booking.exception.exception.ServiceException
import pl.rezerveo.booking.security.encryption.EncryptionService
import pl.rezerveo.booking.user.dto.request.ChangePasswordRequest
import pl.rezerveo.booking.user.dto.request.UpdateUserRequest
import pl.rezerveo.booking.user.model.User
import pl.rezerveo.booking.user.repository.UserRepository
import pl.rezerveo.booking.user.service.impl.UserServiceImpl
import spock.lang.Specification

import static java.util.UUID.randomUUID
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E00000
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E03001
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E03006
import static pl.rezerveo.booking.common.enumerated.ResponseCode.S00000

class UserServiceTest extends Specification {

    def userRepository = Mock(UserRepository)
    def passwordEncoder = Mock(PasswordEncoder)
    def encryptionService = Mock(EncryptionService)

    def userService = new UserServiceImpl(userRepository, passwordEncoder, encryptionService)

    def user = new User(uuid: randomUUID(), email: "user@example.com", password: "encoded-pass")

    def setup() {
        def authentication = new TestingAuthenticationToken(user, null)
        authentication.setAuthenticated(true)
        SecurityContextHolder.getContext().setAuthentication(authentication)
    }

    def cleanup() {
        SecurityContextHolder.clearContext()
    }

    def "getUserByEmail should return user when exists"() {
        given:
        def email = "user@example.com"
        encryptionService.encrypt(_ as String) >> "asdasd23"
        userRepository.findByEmail(email) >> Optional.of(user)

        when:
        def result = userService.getUserByEmail(email)

        then:
        result == user
    }

    def "getUserByEmail should throw exception when user not found"() {
        given:
        def email = "notfound@example.com"
        encryptionService.encrypt(_ as String) >> "asdasd23"
        userRepository.findByEmail(email) >> Optional.empty()

        when:
        userService.getUserByEmail(email)

        then:
        def ex = thrown(ServiceException)
        ex.status == E03001
    }

    def "getUserDetails should return details of logged user"() {
        when:
        def response = userService.getUserDetails()

        then:
        response.email() == user.email
    }

    def "updateUser should update user when data valid"() {
        given:
        def request = new UpdateUserRequest("newFirstName", "NewLastName", "new@example.com")

        def encodedEmail = "asdasd23"
        encryptionService.encrypt(_ as String) >> encodedEmail
        userRepository.isEmailTaken(encodedEmail, user.uuid) >> false

        when:
        def response = userService.updateUser(request)

        then:
        1 * userRepository.save(_ as User)
        response.status == S00000
        user.email == "new@example.com"
    }

    def "updateUser should throw when email already taken"() {
        given:
        def request = new UpdateUserRequest("newFirstName", "NewLastName", "new@example.com")

        def encodedEmail = "asdasd23"
        encryptionService.encrypt(_ as String) >> encodedEmail
        userRepository.isEmailTaken(encodedEmail, user.uuid) >> true

        when:
        userService.updateUser(request)

        then:
        def ex = thrown(ServiceException)
        ex.status == E03006
    }

    def "changePassword should update password when valid"() {
        given:
        def request = new ChangePasswordRequest("newpass", "newpass")

        passwordEncoder.matches(request.password(), user.password) >> false
        passwordEncoder.encode(request.password()) >> "encoded-newpass"

        when:
        def response = userService.changePassword(request)

        then:
        1 * userRepository.save(_ as User)
        user.password == "encoded-newpass"
        response.status == S00000
    }

    def "changePassword should throw when passwords do not match"() {
        given:
        def request = new ChangePasswordRequest("pass1", "pass2")

        when:
        userService.changePassword(request)

        then:
        def ex = thrown(CustomValidationException)
        ex.status == E00000
    }

    def "changePassword should throw when same as current password"() {
        given:
        def request = new ChangePasswordRequest("samepass", "samepass")

        passwordEncoder.matches(request.password(), user.password) >> true

        when:
        userService.changePassword(request)

        then:
        def ex = thrown(CustomValidationException)
        ex.status == E00000
    }
}