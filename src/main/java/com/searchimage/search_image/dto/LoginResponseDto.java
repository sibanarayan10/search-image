package com.searchimage.search_image.dto;

import com.searchimage.search_image.entity.User;

public class LoginResponseDto {

    private String message;
    private String name;


    public LoginResponseDto(String message,String name) {
        this.message = message;
        this.name=name;
    }

    public String getMessage() {
        return message;
    }
    public String getName() {
        return name;
    }
}
