package com.quiz.darkhold.init;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for real-time game communication.
 * CSRF protection for WebSocket is handled by Spring Security's default configuration.
 * The CSRF token must be sent in the CONNECT frame for STOMP over WebSocket.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(final MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        // WebSocket endpoint with SockJS fallback
        // CSRF token will be automatically validated by Spring Security
        registry.addEndpoint("/darkhold-websocket")
                .setAllowedOriginPatterns("*")  // Configure properly in production
                .withSockJS();
    }

}
