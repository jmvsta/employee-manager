package com.jmvstv_v.configuration;

import com.jmvstv_v.dto.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.NoSuchElementException;

@ControllerAdvice("com.hyperoptic.controller")
public class GlobalExceptionHandler {

    private static String getErrorTypeLink(HttpServletRequest request, String errorType) {
        String base = ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(request.getContextPath())
                .build()
                .toUriString();

        return UriComponentsBuilder.fromUriString(base)
                .path("/docs")
                .fragment("errors/" + errorType)
                .build()
                .toUriString();
    }

    @ExceptionHandler({
            NoResourceFoundException.class,
            NoSuchElementException.class
    })
    public ResponseEntity<ErrorDto> handleNotFound(Exception exception, HttpServletRequest request) {
        var errorBody = new ErrorDto(
                getErrorTypeLink(request, "not-found"),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                URI.create(request.getRequestURI()).toString()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(errorBody);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<ErrorDto> handleBadRequest(Exception exception, HttpServletRequest request) {
        var error = new ErrorDto(
                getErrorTypeLink(request, "bad-request"),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage(),
                URI.create(request.getRequestURI()).toString()
        );
        if (exception instanceof DataIntegrityViolationException) {
            error.setDetail("DB Constraint Violation");
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGeneric(Exception exception, HttpServletRequest request) {
        var error = new ErrorDto(
                getErrorTypeLink(request, "internal-server-error"),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                exception.getMessage(),
                URI.create(request.getRequestURI()).toString()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(error);
    }

}
