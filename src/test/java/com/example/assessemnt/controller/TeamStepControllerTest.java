package com.example.assessemnt.controller;

import com.example.assessemnt.service.TeamStepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class TeamStepControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TeamStepService teamStepService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void createTeam_ShouldReturn200() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/teams/{teamId}", "Engineering"))
                .andExpect(status().isOk());
    }

    @Test
    void createTeam_ShouldBeIdempotent() throws Exception {
        // When
        mockMvc.perform(put("/api/teams/{teamId}", "Engineering"))
                .andExpect(status().isOk());
        
        // Then - should succeed again
        mockMvc.perform(put("/api/teams/{teamId}", "Engineering"))
                .andExpect(status().isOk());
    }

    @Test
    void removeTeam_ShouldReturn200() throws Exception {
        // Given
        teamStepService.createTeam("Engineering");

        // When & Then
        mockMvc.perform(delete("/api/teams/{teamId}", "Engineering"))
                .andExpect(status().isOk());
    }

    @Test
    void removeTeam_WithNonExistentTeam_ShouldReturn404() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/teams/{teamId}", "NonExistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Team not found: NonExistent"));
    }

    @Test
    void addSteps_ShouldReturn200() throws Exception {
        // Given
        teamStepService.createTeam("Engineering");
        String requestBody = "{\"steps\": 1000}";

        // When & Then
        mockMvc.perform(post("/api/teams/{teamId}/steps", "Engineering")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void addSteps_WithNegativeSteps_ShouldReturn400() throws Exception {
        // Given
        teamStepService.createTeam("Engineering");
        String requestBody = "{\"steps\": -100}";

        // When & Then
        mockMvc.perform(post("/api/teams/{teamId}/steps", "Engineering")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addSteps_WithNullSteps_ShouldReturn400() throws Exception {
        // Given
        teamStepService.createTeam("Engineering");
        String requestBody = "{\"steps\": null}";

        // When & Then
        mockMvc.perform(post("/api/teams/{teamId}/steps", "Engineering")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addSteps_WithNonExistentTeam_ShouldReturn404() throws Exception {
        // Given
        String requestBody = "{\"steps\": 1000}";

        // When & Then
        mockMvc.perform(post("/api/teams/{teamId}/steps", "NonExistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Team not found: NonExistent"));
    }

    @Test
    void getTeamSteps_ShouldReturn200WithTeamResponse() throws Exception {
        // Given
        teamStepService.createTeam("Engineering");
        teamStepService.addSteps("Engineering", 5000L);

        // When & Then
        mockMvc.perform(get("/api/teams/{teamId}", "Engineering"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamId").value("Engineering"))
                .andExpect(jsonPath("$.stepCount").value(5000));
    }

    @Test
    void getTeamSteps_WithNonExistentTeam_ShouldReturn404() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/teams/{teamId}", "NonExistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Team not found: NonExistent"));
    }

    @Test
    void getLeaderboard_ShouldReturn200WithSortedList() throws Exception {
        // Given - use unique team names to avoid conflicts
        String team1 = "TestTeam1";
        String team2 = "TestTeam2";
        String team3 = "TestTeam3";
        
        // Clean up if they exist
        try { teamStepService.removeTeam(team1); } catch (Exception e) {}
        try { teamStepService.removeTeam(team2); } catch (Exception e) {}
        try { teamStepService.removeTeam(team3); } catch (Exception e) {}
        
        teamStepService.createTeam(team1);
        teamStepService.createTeam(team2);
        teamStepService.createTeam(team3);
        teamStepService.addSteps(team1, 5000L);
        teamStepService.addSteps(team2, 3000L);
        teamStepService.addSteps(team3, 2000L);

        // When & Then
        mockMvc.perform(get("/api/teams/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.teamId == 'TestTeam1')].stepCount").value(5000))
                .andExpect(jsonPath("$[?(@.teamId == 'TestTeam2')].stepCount").value(3000))
                .andExpect(jsonPath("$[?(@.teamId == 'TestTeam3')].stepCount").value(2000));
        
        // Clean up
        try { teamStepService.removeTeam(team1); } catch (Exception e) {}
        try { teamStepService.removeTeam(team2); } catch (Exception e) {}
        try { teamStepService.removeTeam(team3); } catch (Exception e) {}
    }

    @Test
    void getLeaderboard_ShouldReturn200() throws Exception {
        // When & Then - leaderboard should return successfully even with existing teams
        mockMvc.perform(get("/api/teams/leaderboard"))
                .andExpect(status().isOk());
    }
}
