package com.jmvstv_v.service;

import com.jmvstv_v.configuration.props.KeycloakProps;
import com.jmvstv_v.dto.KeycloakTokenResponse;
import com.jmvstv_v.dto.LoginRequest;
import com.jmvstv_v.dto.RefreshTokenRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KeycloakService {

    private final WebClient keycloakWebClient;

    private final KeycloakProps props;

    public KeycloakService(WebClient keycloakWebClient, KeycloakProps props) {
        this.keycloakWebClient = keycloakWebClient;
        this.props = props;
    }

    public Mono<KeycloakTokenResponse> login(LoginRequest loginRequest) {
        return keycloakWebClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", props.realm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("client_id", props.clientId())
                        .with("client_secret", props.clientSecret())
                        .with("grant_type", "password")
                        .with("username", loginRequest.username())
                        .with("password", loginRequest.password())
                        .with("scope", "openid profile email"))
                .retrieve()
                .bodyToMono(KeycloakTokenResponse.class);
    }

    public Mono<KeycloakTokenResponse> refresh(RefreshTokenRequest refreshTokenRequest) {
        return keycloakWebClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", props.realm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("client_id", props.clientId())
                        .with("client_secret", props.clientSecret())
                        .with("grant_type", "refresh_token")
                        .with("refresh_token", refreshTokenRequest.refreshToken()))
                .retrieve()
                .bodyToMono(KeycloakTokenResponse.class);
    }

    public Mono<Void> logout(RefreshTokenRequest refreshTokenRequest) {
        return keycloakWebClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/logout", props.realm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("client_id", props.clientId())
                        .with("client_secret", props.clientSecret())
                        .with("grant_type", "refresh_token")
                        .with("refresh_token", refreshTokenRequest.refreshToken()))
                .retrieve()
                .bodyToMono(Void.class);
    }
}
