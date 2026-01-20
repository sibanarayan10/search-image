package com.searchimage.search_image.dto;

import com.searchimage.search_image.entity.User;

public class LoginResponseDto {

    private String message;
    private User user;


    public LoginResponseDto(String message,User user) {
        this.message = message;
        this.user=user;
    }

    public String getMessage() {
        return message;
    }
    public User getUser() {
        return user;
    }
}
