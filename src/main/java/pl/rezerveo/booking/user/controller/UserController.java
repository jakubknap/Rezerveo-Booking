package pl.rezerveo.booking.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;
import pl.rezerveo.booking.openApi.user.ApiChangePasswordResponse;
import pl.rezerveo.booking.openApi.user.ApiGetUserUserDetailsResponse;
import pl.rezerveo.booking.openApi.user.ApiUpdateUserResponse;
import pl.rezerveo.booking.user.dto.request.ChangePasswordRequest;
import pl.rezerveo.booking.user.dto.request.UpdateUserRequest;
import pl.rezerveo.booking.user.dto.response.UserDetailsResponse;
import pl.rezerveo.booking.user.service.UserService;

import static pl.rezerveo.booking.common.constant.Urls.USERS_URL;

@Slf4j
@RestController
@RequestMapping(USERS_URL)
@RequiredArgsConstructor
@Tag(name = "Zarządzanie użytkownikiem", description = "Operacje związane z użytkownikiem")
public class UserController {

    private final UserService userService;

    @ApiGetUserUserDetailsResponse
    @GetMapping
    public UserDetailsResponse getUserDetails() {
        return userService.getUserDetails();
    }

    @ApiUpdateUserResponse
    @PutMapping
    public BaseResponse updateUser(@RequestBody @Valid UpdateUserRequest request) {
        return userService.updateUser(request);
    }

    @ApiChangePasswordResponse
    @PatchMapping("/change-password")
    public BaseResponse changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        return userService.changePassword(request);
    }
}