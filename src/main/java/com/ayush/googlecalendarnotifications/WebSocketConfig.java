package com.ayush.googlecalendarnotifications;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Clients will subscribe to topics starting with /topic
        config.enableSimpleBroker("/topic");
        // Messages sent from client to server start with /app
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The URL where your client connects (e.g., ws://localhost:8080/ws-calendar)
        registry.addEndpoint("/ws-calendar").setAllowedOrigins("*");
    }
}
