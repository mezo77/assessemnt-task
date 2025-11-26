package com.example.assessemnt.service;

import com.example.assessemnt.dto.LeaderboardEntry;
import com.example.assessemnt.dto.TeamResponse;
import com.example.assessemnt.exception.TeamNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeamStepServiceTest {

    private TeamStepService teamStepService;

    @BeforeEach
    void setUp() {
        teamStepService = new TeamStepService();
    }

    @Test
    void createTeam_ShouldCreateNewTeam() {
        // When
        teamStepService.createTeam("Engineering");

        // Then
        TeamResponse response = teamStepService.getTeamSteps("Engineering");
        assertEquals("Engineering", response.getTeamId());
        assertEquals(0L, response.getStepCount());
    }

    @Test
    void createTeam_ShouldBeIdempotent() {
        // When
        teamStepService.createTeam("Engineering");
        teamStepService.createTeam("Engineering");

        // Then
        TeamResponse response = teamStepService.getTeamSteps("Engineering");
        assertEquals(0L, response.getStepCount());
    }

    @Test
    void createTeam_WithNullTeamId_ShouldThrowException() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            teamStepService.createTeam(null);
        });
    }

    @Test
    void createTeam_WithEmptyTeamId_ShouldThrowException() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            teamStepService.createTeam("");
        });
    }

    @Test
    void createTeam_WithWhitespaceTeamId_ShouldThrowException() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            teamStepService.createTeam("   ");
        });
    }

    @Test
    void removeTeam_ShouldRemoveExistingTeam() {
        // Given
        teamStepService.createTeam("Engineering");

        // When
        teamStepService.removeTeam("Engineering");

        // Then
        assertThrows(TeamNotFoundException.class, () -> {
            teamStepService.getTeamSteps("Engineering");
        });
    }

    @Test
    void removeTeam_WithNonExistentTeam_ShouldThrowException() {
        // Then
        assertThrows(TeamNotFoundException.class, () -> {
            teamStepService.removeTeam("NonExistent");
        });
    }

    @Test
    void removeTeam_WithNullTeamId_ShouldThrowException() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            teamStepService.removeTeam(null);
        });
    }

    @Test
    void addSteps_ShouldIncrementStepCount() {
        // Given
        teamStepService.createTeam("Engineering");

        // When
        teamStepService.addSteps("Engineering", 1000L);
        teamStepService.addSteps("Engineering", 500L);

        // Then
        TeamResponse response = teamStepService.getTeamSteps("Engineering");
        assertEquals(1500L, response.getStepCount());
    }

    @Test
    void addSteps_WithZeroSteps_ShouldNotChangeCount() {
        // Given
        teamStepService.createTeam("Engineering");
        teamStepService.addSteps("Engineering", 1000L);

        // When
        teamStepService.addSteps("Engineering", 0L);

        // Then
        TeamResponse response = teamStepService.getTeamSteps("Engineering");
        assertEquals(1000L, response.getStepCount());
    }

    @Test
    void addSteps_WithNonExistentTeam_ShouldThrowException() {
        // Then
        assertThrows(TeamNotFoundException.class, () -> {
            teamStepService.addSteps("NonExistent", 1000L);
        });
    }

    @Test
    void addSteps_WithNegativeSteps_ShouldThrowException() {
        // Given
        teamStepService.createTeam("Engineering");

        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            teamStepService.addSteps("Engineering", -100L);
        });
    }

    @Test
    void addSteps_WithNullSteps_ShouldThrowException() {
        // Given
        teamStepService.createTeam("Engineering");

        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            teamStepService.addSteps("Engineering", null);
        });
    }

    @Test
    void addSteps_WithNullTeamId_ShouldThrowException() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            teamStepService.addSteps(null, 1000L);
        });
    }

    @Test
    void getTeamSteps_ShouldReturnCorrectStepCount() {
        // Given
        teamStepService.createTeam("Engineering");
        teamStepService.addSteps("Engineering", 5000L);

        // When
        TeamResponse response = teamStepService.getTeamSteps("Engineering");

        // Then
        assertEquals("Engineering", response.getTeamId());
        assertEquals(5000L, response.getStepCount());
    }

    @Test
    void getTeamSteps_WithNonExistentTeam_ShouldThrowException() {
        // Then
        assertThrows(TeamNotFoundException.class, () -> {
            teamStepService.getTeamSteps("NonExistent");
        });
    }

    @Test
    void getTeamSteps_WithNullTeamId_ShouldThrowException() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            teamStepService.getTeamSteps(null);
        });
    }

    @Test
    void getLeaderboard_WithNoTeams_ShouldReturnEmptyList() {
        // When
        List<LeaderboardEntry> leaderboard = teamStepService.getLeaderboard();

        // Then
        assertTrue(leaderboard.isEmpty());
    }

    @Test
    void getLeaderboard_WithSingleTeam_ShouldReturnThatTeam() {
        // Given
        teamStepService.createTeam("Engineering");
        teamStepService.addSteps("Engineering", 1000L);

        // When
        List<LeaderboardEntry> leaderboard = teamStepService.getLeaderboard();

        // Then
        assertEquals(1, leaderboard.size());
        assertEquals("Engineering", leaderboard.get(0).getTeamId());
        assertEquals(1000L, leaderboard.get(0).getStepCount());
    }

    @Test
    void getLeaderboard_ShouldReturnTeamsInDescendingOrder() {
        // Given
        teamStepService.createTeam("Engineering");
        teamStepService.createTeam("Sales");
        teamStepService.createTeam("Marketing");

        teamStepService.addSteps("Engineering", 5000L);
        teamStepService.addSteps("Sales", 3000L);
        teamStepService.addSteps("Marketing", 2000L);

        // When
        List<LeaderboardEntry> leaderboard = teamStepService.getLeaderboard();

        // Then
        assertEquals(3, leaderboard.size());
        assertEquals("Engineering", leaderboard.get(0).getTeamId());
        assertEquals(5000L, leaderboard.get(0).getStepCount());
        assertEquals("Sales", leaderboard.get(1).getTeamId());
        assertEquals(3000L, leaderboard.get(1).getStepCount());
        assertEquals("Marketing", leaderboard.get(2).getTeamId());
        assertEquals(2000L, leaderboard.get(2).getStepCount());
    }

    @Test
    void getLeaderboard_WithTiedScores_ShouldMaintainOrder() {
        // Given
        teamStepService.createTeam("TeamA");
        teamStepService.createTeam("TeamB");
        teamStepService.addSteps("TeamA", 1000L);
        teamStepService.addSteps("TeamB", 1000L);

        // When
        List<LeaderboardEntry> leaderboard = teamStepService.getLeaderboard();

        // Then
        assertEquals(2, leaderboard.size());
        assertEquals(1000L, leaderboard.get(0).getStepCount());
        assertEquals(1000L, leaderboard.get(1).getStepCount());
    }

    @Test
    void concurrentAddSteps_ShouldNotLoseSteps() throws InterruptedException {
        // Given
        teamStepService.createTeam("Engineering");
        int numberOfThreads = 10;
        int stepsPerThread = 1000;
        Thread[] threads = new Thread[numberOfThreads];

        // When
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < stepsPerThread; j++) {
                    teamStepService.addSteps("Engineering", 1L);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Then
        TeamResponse response = teamStepService.getTeamSteps("Engineering");
        assertEquals((long) numberOfThreads * stepsPerThread, response.getStepCount());
    }
}

