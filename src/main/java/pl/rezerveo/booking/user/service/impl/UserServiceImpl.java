package pl.rezerveo.booking.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.rezerveo.booking.exception.dto.BaseApiValidationError;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;
import pl.rezerveo.booking.exception.exception.CustomValidationException;
import pl.rezerveo.booking.exception.exception.ServiceException;
import pl.rezerveo.booking.security.encryption.EncryptionService;
import pl.rezerveo.booking.security.util.SecurityUtils;
import pl.rezerveo.booking.user.dto.request.ChangePasswordRequest;
import pl.rezerveo.booking.user.dto.request.UpdateUserRequest;
import pl.rezerveo.booking.user.dto.response.UserDetailsResponse;
import pl.rezerveo.booking.user.model.User;
import pl.rezerveo.booking.user.repository.UserRepository;
import pl.rezerveo.booking.user.service.UserService;

import java.util.List;
import java.util.UUID;

import static pl.rezerveo.booking.common.enumerated.ResponseCode.E00000;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E03001;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E03006;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.S00000;
import static pl.rezerveo.booking.user.mapper.UserMapper.buildUserDetailsResponse;
import static pl.rezerveo.booking.util.MaskingUtil.maskEmail;
import static pl.rezerveo.booking.util.StringUtil.notEquals;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                             .orElseThrow(() -> {
                                 log.error("User with e-mail: [{}] not found", maskEmail(email));
                                 return new ServiceException(E03001);
                             });
    }

    @Override
    public UserDetailsResponse getUserDetails() {
        User user = SecurityUtils.getLoggedUser();
        log.info("Start the process of retrieving a user details for user with UUID: [{}]", user.getUuid());
        return buildUserDetailsResponse(user);
    }

    @Override
    public BaseResponse updateUser(UpdateUserRequest request) {
        User user = SecurityUtils.getLoggedUser();
        UUID userUuid = user.getUuid();

        log.info("Start the process of updating user with UUID: [{}]", userUuid);

        validateDataAvailability(request, user);
        updateUser(request, user);

        log.info("Successfully completed process of updating user with UUID: [{}]", userUuid);
        return new BaseResponse(S00000);
    }

    @Override
    public BaseResponse changePassword(ChangePasswordRequest request) {
        User user = SecurityUtils.getLoggedUser();
        UUID userUuid = user.getUuid();
        log.info("Start the process of changing password for user with UUID: [{}]", userUuid);

        validatePasswordChangeRequest(request, user);
        updatePassword(request, user);

        log.info("Successfully completed the password change process for the user with UUID: [{}]", userUuid);
        return new BaseResponse(S00000);
    }

    private void validateDataAvailability(UpdateUserRequest request, User user) {
        UUID userUuid = user.getUuid();

        boolean isEmailChanged = notEquals(request.email(), user.getEmail());
        if (isEmailChanged) {
            boolean isEmailTaken = userRepository.isEmailTaken(encryptionService.encrypt(request.email()), userUuid);
            if (isEmailTaken) {
                log.error("User cannot be updated. There is another user with that e-mail.");
                throw new ServiceException(E03006);
            }
        }
    }

    private void updateUser(UpdateUserRequest request, User user) {
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());

        userRepository.save(user);
    }

    private void validatePasswordChangeRequest(ChangePasswordRequest request, User user) {
        if (notEquals(request.password(), request.passwordRepeat())) {
            log.error("Password does not match password repeated");
            throw new CustomValidationException(E00000, List.of(new BaseApiValidationError("passwordRepeat", "passwords are not the same")));
        }

        if (passwordEncoder.matches(request.password(), user.getPassword())) {
            log.error("The password is the same as the user's current password");
            throw new CustomValidationException(E00000, List.of(new BaseApiValidationError("password", "password cannot be the same as the current one")));
        }
    }

    private void updatePassword(ChangePasswordRequest request, User user) {
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }
}