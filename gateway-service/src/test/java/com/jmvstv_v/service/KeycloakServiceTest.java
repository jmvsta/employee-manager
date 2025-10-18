package com.jmvstv_v.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmvstv_v.configuration.props.KeycloakProps;
import com.jmvstv_v.dto.KeycloakTokenResponse;
import com.jmvstv_v.dto.LoginRequest;
import com.jmvstv_v.dto.RefreshTokenRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakServiceTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private WebClient stubClient(ExchangeFunction exchangeFunction) {
        return WebClient.builder().exchangeFunction(exchangeFunction).build();
    }

    @Test
    void login_postsForm_andParsesToken() throws JsonProcessingException {
        var token = new KeycloakTokenResponse("a", "i", 300, 1800, "r", "bearer", 0, "s", "openid");
        var tokenString = objectMapper.writeValueAsString(token);
        ExchangeFunction exchange = request -> {
            assertThat(request.method()).isEqualTo(HttpMethod.POST);
            assertThat(request.url().getPath()).contains("/realms/demo/protocol/openid-connect/token");

            return Mono.just(ClientResponse.create(HttpStatus.OK)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .body(tokenString)
                    .build());
        };

        var props = new KeycloakProps("http://kc", "demo", "cid", "sec");
        var service = new KeycloakService(stubClient(exchange), props);

        var result = service.login(new LoginRequest("u", "p")).block();
        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo("a");
        assertThat(result.refreshToken()).isEqualTo("r");
    }

    @Test
    void refresh_postsForm_andParsesToken() throws JsonProcessingException {
        var token = new KeycloakTokenResponse("na", "i", 300, 1800, "nr", "bearer", 0, "s", "openid");
        var tokenString = objectMapper.writeValueAsString(token);
        ExchangeFunction exchange = request -> Mono.just(ClientResponse.create(HttpStatus.OK)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(tokenString)
                .build());

        var props = new KeycloakProps("http://kc", "demo", "cid", "sec");
        var service = new KeycloakService(stubClient(exchange), props);

        var result = service.refresh(new RefreshTokenRequest("old")).block();
        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo("na");
        assertThat(result.refreshToken()).isEqualTo("nr");
    }

    @Test
    void logout_postsForm_andCompletes() {
        ExchangeFunction exchange = request -> {
            assertThat(request.method()).isEqualTo(HttpMethod.POST);
            assertThat(request.url().getPath()).contains("/realms/demo/protocol/openid-connect/logout");
            return Mono.just(ClientResponse.create(HttpStatus.NO_CONTENT).build());
        };

        var props = new KeycloakProps("http://kc", "demo", "cid", "sec");
        var service = new KeycloakService(stubClient(exchange), props);

        var jwt = Jwt.withTokenValue("t")
                .header("alg", "none")
                .claim("sub", "123")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();

        var mono = service.logout(jwt, new RefreshTokenRequest("r"));
        var result = mono.thenReturn("done").block();
        assertThat(result).isEqualTo("done");
    }
}


