package com.kfd.api.kfd_backend.auth;

import com.kfd.api.kfd_backend.audit.AuditLogService;
import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import com.kfd.api.kfd_backend.user.User;
import com.kfd.api.kfd_backend.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final AuthService authService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiMessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        authService.processForgotPassword(request);
        return ResponseEntity.ok(new ApiMessageResponse(
                HttpStatus.OK.value(),
                "If that email exists in our system, we have sent a password reset link."
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiMessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        try {
            authService.processResetPassword(request);
            return ResponseEntity.ok(new ApiMessageResponse(
                    HttpStatus.OK.value(),
                    "Password successfully reset. You can now login."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiMessageResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage()
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiDataResponse<AuthResponseDTO>> login(
            @RequestBody AuthRequestDTO request,
            HttpServletRequest httpRequest) {

        // Authenticate the user (throws exception if bad credentials)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Fetch user from DB since authentication succeeded
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        // Generate JWT token
        String jwtToken = jwtService.generateToken(user);

        // Record successful login in the audit log (async — non-blocking)
        auditLogService.log(user.getId(), "LOGIN", "USER", httpRequest);

        // Build Response
        AuthResponseDTO responseDTO = AuthResponseDTO.builder()
                .token(jwtToken)
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .build();

        return ResponseEntity.ok(
                new ApiDataResponse<>(
                        HttpStatus.OK.value(),
                        "Login successful",
                        responseDTO
                )
        );
    }
}

