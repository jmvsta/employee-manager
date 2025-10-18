package com.jmvstv_v.dto;

public record KeycloakTokenResponse(
        String accessToken,
        String idToken,
        long expiresIn,
        long refreshExpiresIn,
        String refreshToken,
        String tokenType,
        long notBeforePolicy,
        String sessionState,
        String scope
) {
}

