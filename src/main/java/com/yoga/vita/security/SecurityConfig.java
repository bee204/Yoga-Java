package com.yoga.vita.security;
import com.yoga.vita.entity.User; import com.yoga.vita.repository.UserRepository;
import org.springframework.context.annotation.*; import org.springframework.security.authentication.AuthenticationManager; import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.core.userdetails.*; import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder(12);}
 @Bean UserDetailsService users(UserRepository repository){return username -> {User u=repository.findByEmail(username).or(() -> repository.findByUserName(username)).orElseThrow(() -> new UsernameNotFoundException(username)); return org.springframework.security.core.userdetails.User.withUsername(u.getEmail()==null ? u.getUserName() : u.getEmail()).password(u.getPassword()).roles(u.getRole()==null ? "customer" : u.getRole().toUpperCase()).build();};}
 @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception{return c.getAuthenticationManager();}
 @Bean SecurityFilterChain filter(HttpSecurity http) throws Exception{
  http.csrf(c -> c.disable()).authorizeHttpRequests(a -> a.requestMatchers("/api/public/**","/api/login","/api/ping","/css/**","/js/**","/images/**","/img/**").permitAll().requestMatchers(org.springframework.http.HttpMethod.POST,"/api/registrations").permitAll().requestMatchers("/api/admin/**","/api/teachers/**","/api/teachers","/api/classes/**","/api/classes","/api/customers/**","/api/customers","/admin/**").hasRole("ADMIN").requestMatchers("/api/account/**","/account/**").hasRole("CUSTOMER").anyRequest().permitAll());
  return http.build();
 }
}
