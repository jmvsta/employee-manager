package com.jmvstv_v.handler;

import com.jmvstv_v.component.AuthHandler;
import com.jmvstv_v.component.ExceptionHandler;
import com.jmvstv_v.component.RequestValidator;
import com.jmvstv_v.configuration.AuthRouterConfig;
import com.jmvstv_v.dto.KeycloakTokenResponse;
import com.jmvstv_v.dto.LoginRequest;
import com.jmvstv_v.dto.RefreshTokenRequest;
import com.jmvstv_v.service.KeycloakService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import jakarta.validation.Validation;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthHandlerTest {

    private KeycloakService keycloakService;
    private RequestValidator requestValidator;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        keycloakService = mock(KeycloakService.class);
        requestValidator = new RequestValidator(Validation.buildDefaultValidatorFactory().getValidator());
        var handler = new AuthHandler(keycloakService, requestValidator);
        var errorHandler = new ExceptionHandler();
        var router = new AuthRouterConfig().routes(handler, errorHandler);
        webTestClient = WebTestClient.bindToRouterFunction(router).configureClient().build();
    }

    @Test
    void login_returnsTokenResponse() {
        var token = new KeycloakTokenResponse(
                "access", "id", 300, 1800, "refresh", "bearer", 0, "state", "openid"
        );
        when(keycloakService.login(any(LoginRequest.class))).thenReturn(Mono.just(token));

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new LoginRequest("user", "pass"))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.accessToken").isEqualTo("access")
                .jsonPath("$.refreshToken").isEqualTo("refresh")
                .jsonPath("$.tokenType").isEqualTo("bearer");
        verify(keycloakService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void refresh_returnsTokenResponse() {
        var token = new KeycloakTokenResponse(
                "new-access", "id", 300, 1800, "new-refresh", "bearer", 0, "state", "openid"
        );
        when(keycloakService.refresh(any(RefreshTokenRequest.class))).thenReturn(Mono.just(token));

        webTestClient.post()
                .uri("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RefreshTokenRequest("r"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accessToken").isEqualTo("new-access")
                .jsonPath("$.refreshToken").isEqualTo("new-refresh");
        verify(keycloakService, times(1)).refresh(any(RefreshTokenRequest.class));
    }

    @Test
    void me_returnsUserInfo_fromJwtClaims() throws Exception {
        var jwt = Jwt.withTokenValue("t")
                .header("alg", "none")
                .claim("preferred_username", "u1")
                .claim("email", "u1@example.com")
                .claim("given_name", "U")
                .claim("family_name", "One")
                .claim("roles", List.of("USER", "ADMIN"))
                .build();
        var principal = new JwtAuthenticationToken(jwt);
        var httpRequest = MockServerHttpRequest.get("/auth/me").build();
        var exchange = MockServerWebExchange.from(httpRequest).mutate().principal(Mono.just(principal)).build();
        var request = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());

        var handler = new AuthHandler(keycloakService, requestValidator);
        var responseMono = handler.getCurrentUser(request);
        var response = responseMono.block();
        assert response != null;
        ServerResponse.Context ctx = new ServerResponse.Context() {
            @Override
            public List<HttpMessageWriter<?>> messageWriters() {
                return HandlerStrategies.withDefaults().messageWriters();
            }

            @Override
            public List<ViewResolver> viewResolvers() {
                return HandlerStrategies.withDefaults().viewResolvers();
            }
        };
        response.writeTo(exchange, ctx).block();

        var status = exchange.getResponse().getStatusCode();
        assert status != null && status.is2xxSuccessful();
    }

    @Test
    void logout_returnsNoContent() throws Exception {
        when(keycloakService.logout(any(), any(RefreshTokenRequest.class))).thenReturn(Mono.empty());

        var jwt = Jwt.withTokenValue("t").header("alg", "none").claim("sub", "123").build();
        var principal = new JwtAuthenticationToken(jwt);

        var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        var json = mapper.writeValueAsBytes(new RefreshTokenRequest("r"));
        var httpRequest = MockServerHttpRequest.post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Flux.just(new DefaultDataBufferFactory().wrap(json)));
        var exchange = MockServerWebExchange.from(httpRequest).mutate().principal(Mono.just(principal)).build();
        var request = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());

        var handler = new AuthHandler(keycloakService, requestValidator);
        var response = handler.logout(request).block();
        assert response != null;
        ServerResponse.Context ctx = new ServerResponse.Context() {
            @Override
            public List<HttpMessageWriter<?>> messageWriters() {
                return HandlerStrategies.withDefaults().messageWriters();
            }

            @Override
            public List<ViewResolver> viewResolvers() {
                return HandlerStrategies.withDefaults().viewResolvers();
            }
        };
        response.writeTo(exchange, ctx).block();
        var status = exchange.getResponse().getStatusCode();
        assert status != null;
        assertThat(status.value()).isEqualTo(204);
        verify(keycloakService, times(1)).logout(any(), any(RefreshTokenRequest.class));
    }

    @Test
    void errorHandler_returnsStructuredError_onException() {
        when(keycloakService.login(any(LoginRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("boom")));

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new LoginRequest("u", "p"))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.error").isEqualTo("Internal Server Error")
                .jsonPath("$.message").isEqualTo("boom")
                .jsonPath("$.path").exists();
        verify(keycloakService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void login_unauthorized_fromServiceMapsTo401() {
        when(keycloakService.login(any(LoginRequest.class)))
                .thenReturn(Mono.error(new WebClientResponseException(
                        "unauthorized", 401, "Unauthorized", null, null, null
                )));

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new LoginRequest("u", "p"))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.status").isEqualTo(401)
                .jsonPath("$.error").isEqualTo("Unauthorized");
        verify(keycloakService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void refresh_badRequest_isMappedTo400() {
        webTestClient.post()
                .uri("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RefreshTokenRequest(""))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").value(v -> org.assertj.core.api.Assertions.assertThat(v.toString()).contains("refreshToken"));
        verify(keycloakService, never()).refresh(any(RefreshTokenRequest.class));
    }

    @Test
    void login_validationError_isMappedTo400() {
        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new LoginRequest("", ""))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").value(v -> assertThat(v.toString()).contains("username").contains("password"));
        verify(keycloakService, never()).login(any(LoginRequest.class));
    }

    @Test
    void logout_validationError_isMappedTo400() throws Exception {
        var jwt = Jwt.withTokenValue("t").header("alg", "none").claim("sub", "123").build();
        var principal = new JwtAuthenticationToken(jwt);
        var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        var json = mapper.writeValueAsBytes(new RefreshTokenRequest(""));
        var httpRequest = MockServerHttpRequest.post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Flux.just(new DefaultDataBufferFactory().wrap(json)));
        var exchange = MockServerWebExchange.from(httpRequest).mutate().principal(Mono.just(principal)).build();
        var request = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());

        var handler = new AuthHandler(keycloakService, requestValidator);
        assertThatThrownBy(() -> handler.logout(request).block()).isInstanceOf(ConstraintViolationException.class);
    }
}


