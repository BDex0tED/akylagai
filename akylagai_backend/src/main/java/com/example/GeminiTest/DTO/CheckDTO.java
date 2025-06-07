package com.example.GeminiTest.DTO;

import lombok.Data;

@Data
public class CheckDTO {
    private String userQuestion;
    private String userAnswer;
    private String language;
    private Long sessionId;
}
