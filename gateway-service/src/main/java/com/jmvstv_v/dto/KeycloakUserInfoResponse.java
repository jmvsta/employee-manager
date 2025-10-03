package com.jmvstv_v.dto;

import java.util.List;

public record KeycloakUserInfoResponse(
        String preferredUsername,
        String email,
        String givenName,
        String familyName,
        List<String> roles
) {
}
