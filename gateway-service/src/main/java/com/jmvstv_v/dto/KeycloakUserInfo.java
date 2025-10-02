package com.jmvstv_v.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record KeycloakUserInfo(
        @JsonProperty("preferred_username") String preferredUsername,
        String email,
        @JsonProperty("given_name") String givenName,
        @JsonProperty("family_name") String familyName,
        List<String> roles
) {
}
