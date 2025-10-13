package pl.rezerveo.booking.authentication.service;

import pl.rezerveo.booking.authentication.dto.request.ConfirmResetPasswordRequest;
import pl.rezerveo.booking.authentication.dto.request.ResetPasswordRequest;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;

public interface PasswordService {

    BaseResponse resetPasswordRequest(ResetPasswordRequest request);

    BaseResponse confirmResetPassword(ConfirmResetPasswordRequest request);
}