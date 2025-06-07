package com.example.GeminiTest.DTO;

import lombok.Data;

@Data
public class PreparationPlanDTO {
    private String topic;
    private String prompt;
    private String language;
    private Long sessionId;

}
