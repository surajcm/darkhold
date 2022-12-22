package com.quiz.darkhold.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        //todo : we need to enable CSRF
        http.csrf().disable().httpBasic()
                .and()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(matchingPaths()).anonymous()
                        .anyRequest().authenticated()
                )
                .logout().logoutSuccessUrl("/").invalidateHttpSession(true).deleteCookies("JSESSIONID");
        http.headers().frameOptions().sameOrigin();
        return http.build();
    }

    private String[] matchingPaths() {
        return new String[] {"/", "/home", "/resources/**",
                "/registration", "/images/*", "/logmein",
                "/scripts/*", "/styles/*",
                "/scripts/core/*", "/styles/core/*", "/styles/webfonts/*",
                "/fonts/*", "/favicon.ico",
                "/logme", "/h2-console/*"};
    }

    @Bean
    public AuthenticationManager authManager(final HttpSecurity http,
                                             final BCryptPasswordEncoder bCryptPasswordEncoder)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }
}
