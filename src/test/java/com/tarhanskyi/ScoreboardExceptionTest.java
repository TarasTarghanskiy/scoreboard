package com.tarhanskyi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.tarhanskyi.Constants.MATCHES_LIMIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ScoreboardExceptionTest {

    @Test
    @DisplayName("should throw when match already exists for team")
    void shouldThrowForMatchExistsForTeam() {
        String team = "Real Madrid";

        ScoreboardException ex = assertThrows(ScoreboardException.class, () -> {
            ScoreboardException.matchExistsFor(team);
        });

        assertEquals("Match already exists for a team: Real Madrid", ex.getMessage());
    }

    @Test
    @DisplayName("should throw when match is not found by ID")
    void shouldThrowWhenMatchNotFound() {
        UUID id = UUID.randomUUID();

        ScoreboardException ex = assertThrows(ScoreboardException.class, () -> {
            ScoreboardException.notFoundById(id, true);
        });

        assertEquals("Match not found with ID: " + id, ex.getMessage());
    }

    @Test
    @DisplayName("should NOT throw when match is found")
    void shouldNotThrowWhenMatchFound() {
        UUID id = UUID.randomUUID();

        assertDoesNotThrow(() -> ScoreboardException.notFoundById(id, false));
    }

    @Test
    @DisplayName("should throw when match limit is reached")
    void shouldThrowWhenMatchLimitReached() {
        ScoreboardException ex = assertThrows(ScoreboardException.class, () -> {
            ScoreboardException.matchLimitReached(MATCHES_LIMIT);
        });
        assertEquals("Too many active matches, current: 100, limit: " + MATCHES_LIMIT, ex.getMessage());

        ex = assertThrows(ScoreboardException.class, () -> {
            ScoreboardException.matchLimitReached(MATCHES_LIMIT + 1);
        });
        assertEquals("Too many active matches, current: 101, limit: " + MATCHES_LIMIT, ex.getMessage());
    }

    @Test
    @DisplayName("should NOT throw when match count is below limit")
    void shouldNotThrowWhenBelowLimit() {
        assertDoesNotThrow(() -> {
            ScoreboardException.matchLimitReached(MATCHES_LIMIT - 2);
            ScoreboardException.matchLimitReached(MATCHES_LIMIT - 1);
        });
    }
}
