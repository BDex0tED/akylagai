package com.example.GeminiTest.Security.DTO;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String errorMessage;
    private String tokenType = "Bearer ";

    public AuthResponseDTO(String accessToken, String refreshToken, String errorMessage) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.errorMessage = errorMessage;
    }
}
