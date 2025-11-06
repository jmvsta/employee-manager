package com.jmvstv_v.component;

import com.jmvstv_v.dto.KeycloakUserInfoResponse;
import com.jmvstv_v.dto.LoginRequest;
import com.jmvstv_v.dto.RefreshTokenRequest;
import com.jmvstv_v.service.KeycloakService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class AuthHandler {

    private final KeycloakService keycloakService;
    private final RequestValidator requestValidator;

    public AuthHandler(KeycloakService keycloakService, RequestValidator requestValidator) {
        this.keycloakService = keycloakService;
        this.requestValidator = requestValidator;
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(keycloakService::login)
                .flatMap(token -> ServerResponse.ok().bodyValue(token));
    }

    public Mono<ServerResponse> refresh(ServerRequest request) {
        return request.bodyToMono(RefreshTokenRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(keycloakService::refresh)
                .flatMap(token -> ServerResponse.ok().bodyValue(token));
    }

    public Mono<ServerResponse> getCurrentUser(ServerRequest request) {
        return request.principal()
                .cast(JwtAuthenticationToken.class)
                .flatMap(jwt -> {
                    var token = jwt.getToken();
                    var userInfo = new KeycloakUserInfoResponse(
                            token.getClaimAsString("preferred_username"),
                            token.getClaimAsString("email"),
                            token.getClaimAsString("given_name"),
                            token.getClaimAsString("family_name"),
                            token.getClaimAsStringList("roles")
                    );
                    return ServerResponse.ok().bodyValue(userInfo);
                });
    }

    public Mono<ServerResponse> logout(ServerRequest request) {
        return request.principal()
                .cast(JwtAuthenticationToken.class)
                .flatMap(jwt ->
                        request.bodyToMono(RefreshTokenRequest.class)
                                .flatMap(requestValidator::validate)
                                .flatMap(refreshTokenRequest ->
                                        keycloakService.logout(jwt.getToken(), refreshTokenRequest))
                                .then(ServerResponse.noContent().build()));
    }
}
