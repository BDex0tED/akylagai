package com.example.GeminiTest.Security.DTO;

import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String password;
    private String email;
    private String role;
}