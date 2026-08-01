package com.mukesh.userservice.Config;

import com.mukesh.userservice.Service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final CustomUserDetailsService customUserDetailsService;
    public SecurityConfiguration(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain  securityFilterChain(HttpSecurity http) throws Exception {
        SecurityFilterChain filrerchain=http
                .csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationManager(autheticationmanager())
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(HttpMethod.POST,"/user/login/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/user/register/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/user/forgotpassword/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/user/resetpassword/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/user/getuser").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .build();
        return filrerchain;
    }
    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager autheticationmanager(){
        DaoAuthenticationProvider dao=new DaoAuthenticationProvider(customUserDetailsService);
        dao.setPasswordEncoder(encoder());
        ProviderManager providerManager=new ProviderManager(dao);
        return providerManager;
    }

}
