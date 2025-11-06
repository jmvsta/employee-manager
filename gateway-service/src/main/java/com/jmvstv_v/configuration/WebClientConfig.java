package com.jmvstv_v.configuration;

import com.jmvstv_v.configuration.props.KeycloakProps;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private final KeycloakProps keycloakProps;

    public WebClientConfig(KeycloakProps keycloakProps) {
        this.keycloakProps = keycloakProps;
    }

    @Bean
    public WebClient keycloakWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(keycloakProps.url())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
