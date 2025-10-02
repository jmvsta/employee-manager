package com.jmvstv_v.configuration;

import com.jmvstv_v.component.GlobalExceptionHandler;
import com.jmvstv_v.handler.AuthHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AuthRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(
            AuthHandler handler,
            GlobalExceptionHandler errorHandler
    ) {
        return RouterFunctions.route()
                .POST("/auth/login", handler::login)
                .POST("/auth/refresh", handler::refresh)
                .GET("/auth/me", handler::getCurrentUser)
                .POST("/auth/logout", handler::logout)
                .filter(errorHandler)
                .build();
    }
}