package pl.rezerveo.booking.user.service;

import pl.rezerveo.booking.exception.dto.response.BaseResponse;
import pl.rezerveo.booking.user.dto.request.ChangePasswordRequest;
import pl.rezerveo.booking.user.dto.request.UpdateUserRequest;
import pl.rezerveo.booking.user.dto.response.UserDetailsResponse;
import pl.rezerveo.booking.user.model.User;

public interface UserService {

    User getUserByEmail(String email);

    UserDetailsResponse getUserDetails();

    BaseResponse updateUser(UpdateUserRequest request);

    BaseResponse changePassword(ChangePasswordRequest request);
}