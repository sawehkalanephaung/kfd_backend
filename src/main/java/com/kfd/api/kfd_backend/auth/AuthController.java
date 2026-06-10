package com.kfd.api.kfd_backend.auth;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import com.kfd.api.kfd_backend.user.User;
import com.kfd.api.kfd_backend.user.UserRepository;
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

    @PostMapping("/login")
    public ResponseEntity<ApiDataResponse<AuthResponseDTO>> login(@RequestBody AuthRequestDTO request) {
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
