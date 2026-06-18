package com.kfd.api.kfd_backend.auth;

public interface AuthService {
    void processForgotPassword(ForgotPasswordRequestDTO request);
    void processResetPassword(ResetPasswordRequestDTO request);
}
