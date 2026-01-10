package com.searchimage.search_image.dto;

public class LoginResponseDto {

    private String message;

    public LoginResponseDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
