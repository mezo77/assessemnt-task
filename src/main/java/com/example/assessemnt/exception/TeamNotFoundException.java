package com.example.assessemnt.exception;

public class TeamNotFoundException extends RuntimeException {
    
    public TeamNotFoundException(String teamId) {
        super("Team not found: " + teamId);
    }
}

