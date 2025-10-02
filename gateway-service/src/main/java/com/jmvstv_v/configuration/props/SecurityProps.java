package com.jmvstv_v.configuration.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.jwt")
public record SecurityProps(String jwkSetUri, String issuerUri) {}