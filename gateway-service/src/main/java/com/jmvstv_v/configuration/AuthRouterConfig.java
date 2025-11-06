package com.jmvstv_v.configuration;

import com.jmvstv_v.component.ExceptionHandler;
import com.jmvstv_v.component.AuthHandler;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AuthRouterConfig {

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/auth/login", method = RequestMethod.POST,
                    beanClass = AuthHandler.class, beanMethod = "login",
                    operation = @Operation(operationId = "login", summary = "User login")),
            @RouterOperation(path = "/auth/refresh", method = RequestMethod.POST,
                    beanClass = AuthHandler.class, beanMethod = "refresh",
                    operation = @Operation(operationId = "refresh", summary = "Refresh token")),
            @RouterOperation(path = "/auth/me", method = RequestMethod.GET,
                    beanClass = AuthHandler.class, beanMethod = "getCurrentUser",
                    operation = @Operation(operationId = "getCurrentUser", summary = "Get current user info")),
            @RouterOperation(path = "/auth/logout", method = RequestMethod.POST,
                    beanClass = AuthHandler.class, beanMethod = "logout",
                    operation = @Operation(operationId = "logout", summary = "Logout"))
    })
    public RouterFunction<ServerResponse> routes(
            AuthHandler handler,
            ExceptionHandler errorHandler
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