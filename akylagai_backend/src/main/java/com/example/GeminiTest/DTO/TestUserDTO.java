package com.example.GeminiTest.DTO;

import lombok.Data;

@Data
public class TestUserDTO {
    private String questionsTopic;
    private String language;
    private String questionsNum;
    private String difficulty;
    private Long sessionId;

}
