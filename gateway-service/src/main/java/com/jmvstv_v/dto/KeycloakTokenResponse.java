package com.jmvstv_v.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakTokenResponse(
        String accessToken,
        long expiresIn,
        long refreshExpiresIn,
        String refreshToken,
        String tokenType,
        long notBeforePolicy,
        String sessionState,
        String scope
) {
}

