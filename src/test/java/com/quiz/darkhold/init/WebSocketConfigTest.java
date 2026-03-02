package com.quiz.darkhold.init;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.SockJsServiceRegistration;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("WebSocketConfig Tests")
class WebSocketConfigTest {

    private WebSocketConfig config;

    @BeforeEach
    void setUp() {
        config = new WebSocketConfig();
    }

    @Test
    @DisplayName("Should enable simple broker for /topic")
    void shouldEnableSimpleBrokerForTopic() {
        var registry = mock(MessageBrokerRegistry.class);
        config.configureMessageBroker(registry);
        verify(registry).enableSimpleBroker("/topic");
    }

    @Test
    @DisplayName("Should set app destination prefix to /app")
    void shouldSetAppDestinationPrefix() {
        var registry = mock(MessageBrokerRegistry.class);
        config.configureMessageBroker(registry);
        verify(registry).setApplicationDestinationPrefixes("/app");
    }

    @Test
    @DisplayName("Should register darkhold-websocket endpoint")
    void shouldRegisterWebSocketEndpoint() {
        var registry = mock(StompEndpointRegistry.class);
        var registration = mock(StompWebSocketEndpointRegistration.class);
        var sockJs = mock(SockJsServiceRegistration.class);
        when(registry.addEndpoint(anyString())).thenReturn(registration);
        when(registration.setAllowedOriginPatterns(anyString())).thenReturn(registration);
        when(registration.withSockJS()).thenReturn(sockJs);

        config.registerStompEndpoints(registry);

        verify(registry).addEndpoint("/darkhold-websocket");
    }

    @Test
    @DisplayName("Should set allowed origin patterns to wildcard")
    void shouldSetAllowedOriginPatterns() {
        var registry = mock(StompEndpointRegistry.class);
        var registration = mock(StompWebSocketEndpointRegistration.class);
        var sockJs = mock(SockJsServiceRegistration.class);
        when(registry.addEndpoint(anyString())).thenReturn(registration);
        when(registration.setAllowedOriginPatterns(anyString())).thenReturn(registration);
        when(registration.withSockJS()).thenReturn(sockJs);

        config.registerStompEndpoints(registry);

        verify(registration).setAllowedOriginPatterns("*");
    }

    @Test
    @DisplayName("Should enable SockJS fallback")
    void shouldEnableSockJsFallback() {
        var registry = mock(StompEndpointRegistry.class);
        var registration = mock(StompWebSocketEndpointRegistration.class);
        var sockJs = mock(SockJsServiceRegistration.class);
        when(registry.addEndpoint(anyString())).thenReturn(registration);
        when(registration.setAllowedOriginPatterns(anyString())).thenReturn(registration);
        when(registration.withSockJS()).thenReturn(sockJs);

        config.registerStompEndpoints(registry);

        verify(registration).withSockJS();
    }
}
