package com.example.assessemnt.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddStepsRequest {
    
    @NotNull(message = "Steps cannot be null")
    @Min(value = 0, message = "Steps cannot be negative")
    private Long steps;

    public AddStepsRequest() {
    }

    public AddStepsRequest(Long steps) {
        this.steps = steps;
    }

    public Long getSteps() {
        return steps;
    }

    public void setSteps(Long steps) {
        this.steps = steps;
    }
}

