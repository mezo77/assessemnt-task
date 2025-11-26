package com.example.assessemnt.service;

import com.example.assessemnt.dto.LeaderboardEntry;
import com.example.assessemnt.dto.TeamResponse;
import com.example.assessemnt.exception.TeamNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TeamStepService {

    private final ConcurrentHashMap<String, AtomicLong> teams = new ConcurrentHashMap<>();

    public void createTeam(String teamId) {
        if (teamId == null || teamId.trim().isEmpty()) {
            throw new IllegalArgumentException("Team ID cannot be null or empty");
        }
        teams.putIfAbsent(teamId, new AtomicLong(0));
    }

    public void removeTeam(String teamId) {
        if (teamId == null || teamId.trim().isEmpty()) {
            throw new IllegalArgumentException("Team ID cannot be null or empty");
        }
        AtomicLong removed = teams.remove(teamId);
        if (removed == null) {
            throw new TeamNotFoundException(teamId);
        }
    }

    public void addSteps(String teamId, Long steps) {
        if (teamId == null || teamId.trim().isEmpty()) {
            throw new IllegalArgumentException("Team ID cannot be null or empty");
        }
        if (steps == null || steps < 0) {
            throw new IllegalArgumentException("Steps must be non-negative");
        }
        
        AtomicLong stepCount = teams.get(teamId);
        if (stepCount == null) {
            throw new TeamNotFoundException(teamId);
        }
        
        stepCount.addAndGet(steps);
    }

    public TeamResponse getTeamSteps(String teamId) {
        if (teamId == null || teamId.trim().isEmpty()) {
            throw new IllegalArgumentException("Team ID cannot be null or empty");
        }
        
        AtomicLong stepCount = teams.get(teamId);
        if (stepCount == null) {
            throw new TeamNotFoundException(teamId);
        }
        
        return new TeamResponse(teamId, stepCount.get());
    }

    public List<LeaderboardEntry> getLeaderboard() {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        
        teams.forEach((teamId, stepCount) -> {
            leaderboard.add(new LeaderboardEntry(teamId, stepCount.get()));
        });
        
        leaderboard.sort(Comparator.comparing(LeaderboardEntry::getStepCount).reversed());
        
        return leaderboard;
    }
}

