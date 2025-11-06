package com.jmvstv_v.configuration.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak")
public record KeycloakProps(String url, String realm, String clientId, String clientSecret) {}