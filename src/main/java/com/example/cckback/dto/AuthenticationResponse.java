package com.example.cckback.dto;

public class AuthenticationResponse {

    private String token;

    // Constructeur avec un argument (token)
    public AuthenticationResponse(String token) {
        this.token = token;
    }

    // Getter
    public String getToken() {
        return token;
    }

    // Setter
    public void setToken(String token) {
        this.token = token;
    }
}
