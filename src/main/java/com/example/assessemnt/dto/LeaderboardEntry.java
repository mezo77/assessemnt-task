package com.example.assessemnt.dto;

public class LeaderboardEntry {
    
    private String teamId;
    private Long stepCount;

    public LeaderboardEntry() {
    }

    public LeaderboardEntry(String teamId, Long stepCount) {
        this.teamId = teamId;
        this.stepCount = stepCount;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public Long getStepCount() {
        return stepCount;
    }

    public void setStepCount(Long stepCount) {
        this.stepCount = stepCount;
    }
}

