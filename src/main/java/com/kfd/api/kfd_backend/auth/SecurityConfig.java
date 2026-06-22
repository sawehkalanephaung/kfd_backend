package com.kfd.api.kfd_backend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// TODO: REVERT BEFORE PROD! Security is temporarily disabled for frontend development.
// To re-enable: uncomment all commented lines, re-add @EnableMethodSecurity,
// inject jwtAuthFilter & authenticationProvider, and restore the original authorizeHttpRequests block.
@Configuration
@EnableWebSecurity
// @EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // TODO: REVERT BEFORE PROD! — Re-inject these when re-enabling security
    // private final JwtAuthenticationFilter jwtAuthFilter;
    // private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                // TODO: REVERT BEFORE PROD! — Original auth rules below, restore this block:
                // .authorizeHttpRequests(req ->
                //         req.requestMatchers("/api/v1/public/**").permitAll()
                //                 .requestMatchers("/api/v1/auth/**").permitAll()
                //                 .requestMatchers("/uploads/**").permitAll()
                //                 .requestMatchers("/api/v1/admin/**").authenticated()
                //                 .anyRequest().authenticated()
                // )
                // .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // .authenticationProvider(authenticationProvider)
                // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(req -> req.anyRequest().permitAll());

        return http.build();
    }
}
