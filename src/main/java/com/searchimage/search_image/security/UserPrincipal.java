package com.searchimage.search_image.security;

public class UserPrincipal {
    public UserPrincipal(String email,Long userId){
        this.userId=userId;
        this.email=email;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private Long userId;
    private String email;

}
