package com.jmvstv_v.dto;

public record ErrorResponse(int status, String error, String message, String path) {
}
