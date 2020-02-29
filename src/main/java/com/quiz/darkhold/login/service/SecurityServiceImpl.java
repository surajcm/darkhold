package com.quiz.darkhold.login.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SecurityServiceImpl implements SecurityService {
    private static final String UNREGISTERED_USER = "UNREGISTERED_USER";
    private static final String ROLE_MODERATOR = "ROLE_MODERATOR";
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);

    @Override
    public String findLoggedInUsername() {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (userDetails instanceof UserDetails) {
            return ((UserDetails) userDetails).getUsername();
        }

        return null;
    }

    @Override
    public void autoLogin(String username, String password) {
        UserDetails userDetails;
        boolean unRegistered = password.equalsIgnoreCase(UNREGISTERED_USER);
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (unRegistered) {
            userDetails = new User(username, UNREGISTERED_USER, authorities);
        } else {
            userDetails = userDetailsService.loadUserByUsername(username);
            // this is real user
            if (userDetails.getAuthorities() == null || userDetails.getAuthorities().isEmpty()) {
                logger.info("empty authorities found, adding moderator role");
                authorities.add(new SimpleGrantedAuthority(ROLE_MODERATOR));
                userDetails = new User(userDetails.getUsername(), userDetails.getPassword(), authorities);
            } else {
                authorities = (List<GrantedAuthority>) userDetails.getAuthorities();
            }
        }
        logger.info("Successfully fetched user details : " + userDetails);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(userDetails, password, authorities);
        if (!unRegistered) {
            authenticationManager.authenticate(token);
        }

        if (token.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(token);
            logger.info(String.format("Auto login %s successfully!", username));
        }
    }
}
