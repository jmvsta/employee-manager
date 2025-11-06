package com.jmvstv_v.component;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

class ExceptionHandlerTest {

    @Test
    void filter_convertsExceptionsToErrorResponse() {
        var errorHandler = new ExceptionHandler();
        RouterFunction<ServerResponse> routes = RouterFunctions.route()
                .GET("/boom", req -> Mono.error(new IllegalStateException("bad")))
                .filter(errorHandler)
                .build();

        WebTestClient.bindToRouterFunction(routes)
                .build()
                .get()
                .uri("/boom")
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.message").isEqualTo("bad")
                .jsonPath("$.path").isEqualTo("/boom");
    }

    @Test
    void filter_mapsIllegalArgumentTo400() {
        var errorHandler = new ExceptionHandler();
        var routes = RouterFunctions.route()
                .GET("/bad", req -> Mono.error(new IllegalArgumentException("oops")))
                .filter(errorHandler)
                .build();

        WebTestClient.bindToRouterFunction(routes)
                .build()
                .get().uri("/bad").exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("oops");
    }

    @Test
    void filter_mapsWebClientResponseExceptionStatus() {
        var errorHandler = new ExceptionHandler();
        var routes = RouterFunctions.route()
                .GET("/unauth", req -> Mono.error(new org.springframework.web.reactive.function.client.WebClientResponseException(
                        "unauthorized", 401, "Unauthorized", null, null, null
                )))
                .filter(errorHandler)
                .build();

        WebTestClient.bindToRouterFunction(routes)
                .build()
                .get().uri("/unauth").exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.status").isEqualTo(401)
                .jsonPath("$.error").isEqualTo("Unauthorized");
    }
}


