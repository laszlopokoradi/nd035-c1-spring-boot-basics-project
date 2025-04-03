package com.udacity.jwdnd.course1.cloudstorage.config;


import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.web.*;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationService authenticationService;

    public SecurityConfig(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(httpForm ->{
                    httpForm.loginPage("/login").permitAll();
                    httpForm.defaultSuccessUrl("/home", true);

                })
                .logout(httpLogout -> httpLogout.logoutUrl("/logout"))
                .authorizeHttpRequests(registry ->{
                    registry.requestMatchers("/signup","/css/**","/js/**").permitAll();
                    registry.anyRequest().authenticated();
                })
                .authenticationProvider(authenticationService)
                .build();
    }
}