package com.quiz.darkhold.init;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(final UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http,
                                           final HandlerMappingIntrospector introspect) throws Exception {
        //todo : we need to enable CSRF
        http.csrf(AbstractHttpConfigurer::disable);
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspect);
        for (var paths : matchingPaths()) {
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers(mvcMatcherBuilder.pattern(paths)).permitAll()
            );
        }
        http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
        http.formLogin((formLogin) -> {
            formLogin.defaultSuccessUrl("/", true).permitAll();
        });
        http.logout((logout) -> logout.logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID"));

        http.headers(
                (header) -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        );
        return http.build();
    }

    private String[] matchingPaths() {
        return new String[]{"/", "/logmein",
                "/home", "/resources/**",
                "/registration", "/images/**",
                "/scripts/**", "/styles/**",
                "/scripts/core/**", "/styles/core/*", "/styles/webfonts/**",
                "/fonts/**", "/favicon.ico",
                "/logme", "/h2-console/**",
                "/enterGame", "/joinGame"
        };
    }

    @Bean
    public AuthenticationManager authManager(final BCryptPasswordEncoder bCryptPasswordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(authProvider);
    }
}
