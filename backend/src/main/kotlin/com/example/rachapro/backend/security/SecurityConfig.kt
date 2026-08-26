package com.example.rachapro.backend.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity
    ): SecurityFilterChain {

        http
            .csrf { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/users"
                    ).permitAll()
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/auth/login"
                    ).permitAll()
                    .requestMatchers(
                        "/actuator/health",
                        "/actuator/health/**"
                    ).permitAll()
                    .requestMatchers(
                        "/api/**"
                    ).authenticated()
                    .anyRequest().permitAll()
            }
            .oauth2ResourceServer {
                it.jwt { }
            }

        return http.build()
    }
}