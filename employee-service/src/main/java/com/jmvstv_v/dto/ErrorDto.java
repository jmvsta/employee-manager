package com.jmvstv_v.dto;

import java.util.Objects;

public class ErrorDto {

    private String type;

    private int code;

    private String title;

    private String detail;

    private String instance;

    public ErrorDto() {
    }

    public ErrorDto(String type, int code, String title, String detail, String instance) {
        this.type = type;
        this.code = code;
        this.title = title;
        this.detail = detail;
        this.instance = instance;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ErrorDto errorDto)) return false;
        return code == errorDto.code && Objects.equals(type, errorDto.type) && Objects.equals(title, errorDto.title) && Objects.equals(detail, errorDto.detail) && Objects.equals(instance, errorDto.instance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, code, title, detail, instance);
    }
}
