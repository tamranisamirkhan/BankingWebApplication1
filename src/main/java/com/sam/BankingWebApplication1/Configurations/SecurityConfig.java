package com.sam.BankingWebApplication1.Configurations;

import com.sam.BankingWebApplication1.Security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1️⃣ Stateless JWT-based security
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 2️⃣ Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // 🔓 PUBLIC ENDPOINTS (no token)
                        .requestMatchers(
                                "/smartBank/customer/createCustomer",
                                "/smartBank/user/login",
                                "/smartBank/user/activate/**"
                        ).permitAll()

                        // 🔐 TEMP KYC TOKEN ONLY
                        .requestMatchers("/smartBank/customer/upload/kyc")
                        .hasRole("KYC_PENDING")

                        // 🔐 CUSTOMER APIs (after login)
                        .requestMatchers("/smartBank/customer/**")
                        .hasRole("CUSTOMER")

                        // 🔐 ADMIN APIs
                        .requestMatchers("/smartBank/admin/**")
                        .hasRole("ADMIN")

                        // 🔒 EVERYTHING ELSE
                        .anyRequest().authenticated()
                )

                // 3️⃣ JWT filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Required for login
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
