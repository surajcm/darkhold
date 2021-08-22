package com.quiz.darkhold.login.service;

import com.quiz.darkhold.util.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.HashSet;
import java.util.Set;

@Service
public class SecurityServiceImpl implements SecurityService {
    private static final String UNREGISTERED_USER = "UNREGISTERED_USER";
    private static final String ROLE_MODERATOR = "ROLE_MODERATOR";
    private final Logger logger = LogManager.getLogger(SecurityServiceImpl.class);
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public String findLoggedInUsername() {
        var userDetails = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (userDetails instanceof UserDetails details) {
            return details.getUsername();
        }
        return null;
    }

    @Override
    public void autoLogin(final String username, final String password) {
        var unRegistered = password.equalsIgnoreCase(UNREGISTERED_USER);
        var userDetails = getUserDetails(username, unRegistered);
        var authorities = (Set<GrantedAuthority>) userDetails.getAuthorities();
        logger.info("Successfully fetched user details : {} ", userDetails);
        var token = new UsernamePasswordAuthenticationToken(userDetails, password, authorities);
        if (!unRegistered) {
            authenticationManager.authenticate(token);
        }
        if (token.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(token);
            var sanitizedUserName = CommonUtils.sanitizedString(username);
            logger.info("Auto login %s successfully! : {}", sanitizedUserName);
        }
    }

    private UserDetails getUserDetails(final String username, final boolean unRegistered) {
        UserDetails userDetails;
        if (unRegistered) {
            userDetails = new User(username, UNREGISTERED_USER, new ArrayList<>());
        } else {
            userDetails = userDetailsService.loadUserByUsername(username);
            // this is real user
            if (userDetails.getAuthorities() == null || userDetails.getAuthorities().isEmpty()) {
                userDetails = new User(userDetails.getUsername(),
                        userDetails.getPassword(),
                        populateAuthorities());
            }
        }
        return userDetails;
    }

    private Set<GrantedAuthority> populateAuthorities() {
        logger.info("empty authorities found, adding moderator role");
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(ROLE_MODERATOR));
        return authorities;
    }
}
