package com.example.GeminiTest.DTO;

import lombok.Data;

@Data
public class PromptDTO {
    private String prompt;
    private String language;
    private String typeOfResponse;
    private String responseMode;
    private Long sessionId;

}
