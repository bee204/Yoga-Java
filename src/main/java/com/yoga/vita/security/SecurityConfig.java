package com.yoga.vita.security;

import com.yoga.vita.entity.User;
import com.yoga.vita.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public UserDetailsService users(UserRepository repository) {
        return username -> {
            User u = repository.findByEmail(username)
                    .or(() -> repository.findByUserName(username))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            String roleName = u.getRole() == null ? "CUSTOMER" : u.getRole().trim().toUpperCase();
            String loginName = u.getEmail() != null && !u.getEmail().isBlank() ? u.getEmail() : u.getUserName();

            return org.springframework.security.core.userdetails.User.withUsername(loginName)
                    .password(u.getPassword())
                    .roles(roleName)
                    .build();
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception {
        return c.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filter(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Keep disabled for easy API & form testing
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/css/**", "/js/**", "/img/**", "/build/**", "/storage/**", "/favicon.ico",
                    "/", "/classes/**", "/teachers/**", "/authors", "/team", "/members", "/contact",
                    "/account/login", "/account/register", "/teacher/login", "/admin/login",
                    "/api/ping", "/api/public/**", "/api/login"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/register", "/api/registrations").permitAll()
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/teacher/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers("/account/**", "/registered-classes/**").hasAnyRole("CUSTOMER", "ADMIN", "TEACHER")
                .anyRequest().permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/account/logout")
                .logoutSuccessUrl("/?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
