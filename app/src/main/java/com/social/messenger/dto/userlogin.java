package com.social.messenger.dto;

public class userlogin {
    private String id;
    private String email;
    private String password;
    private String token;

    public userlogin() {
    }

    public userlogin(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public userlogin(String token, String password, String email, String id) {
        this.token = token;
        this.password = password;
        this.email = email;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
