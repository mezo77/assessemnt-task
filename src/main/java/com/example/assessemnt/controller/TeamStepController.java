package com.example.assessemnt.controller;

import com.example.assessemnt.dto.AddStepsRequest;
import com.example.assessemnt.dto.LeaderboardEntry;
import com.example.assessemnt.dto.TeamResponse;
import com.example.assessemnt.service.TeamStepService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamStepController {

    private final TeamStepService teamStepService;

    public TeamStepController(TeamStepService teamStepService) {
        this.teamStepService = teamStepService;
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<Void> createTeam(@PathVariable String teamId) {
        teamStepService.createTeam(teamId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> removeTeam(@PathVariable String teamId) {
        teamStepService.removeTeam(teamId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{teamId}/steps")
    public ResponseEntity<Void> addSteps(
            @PathVariable String teamId,
            @Valid @RequestBody AddStepsRequest request) {
        teamStepService.addSteps(teamId, request.getSteps());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResponse> getTeamSteps(@PathVariable String teamId) {
        TeamResponse response = teamStepService.getTeamSteps(teamId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard() {
        List<LeaderboardEntry> leaderboard = teamStepService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }
}

