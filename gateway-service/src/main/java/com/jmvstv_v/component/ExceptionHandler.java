package com.jmvstv_v.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import com.jmvstv_v.dto.ErrorResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import jakarta.validation.ConstraintViolationException;

@Component
public class ExceptionHandler implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
        return next.handle(request)
                .onErrorResume(ex -> {
                            log.error("Error while processing [{}]: {}", request.uri(), ex.toString(), ex);
                            HttpStatus status = mapStatus(ex);

                            var errorBody = new ErrorResponse(
                                    status.value(),
                                    status.getReasonPhrase(),
                                    ex.getMessage(),
                                    request.uri().toASCIIString()
                            );

                            return ServerResponse.status(status)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(errorBody);
                        }
                );
    }

    private HttpStatus mapStatus(Throwable ex) {
        return switch (ex) {
            case WebClientResponseException wce -> {
                var code = wce.getStatusCode().value();
                yield HttpStatus.resolve(code) != null ? HttpStatus.valueOf(code) : HttpStatus.BAD_GATEWAY;
            }
            case ConstraintViolationException ignored -> HttpStatus.BAD_REQUEST;
            case ServerWebInputException ignored -> HttpStatus.BAD_REQUEST;
            case ResponseStatusException rse -> HttpStatus.valueOf(rse.getStatusCode().value());
            case IllegalArgumentException ignored -> HttpStatus.BAD_REQUEST;
            case IllegalStateException ignored -> HttpStatus.CONFLICT;
            case AccessDeniedException ignored -> HttpStatus.FORBIDDEN;
            case AuthenticationException ignored -> HttpStatus.UNAUTHORIZED;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}

