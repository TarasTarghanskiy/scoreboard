package com.tarhanskyi;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

import static com.tarhanskyi.Constants.TEAM_NAME_LENGTH_LIMIT;
import static com.tarhanskyi.Constants.TEAM_NAME_REGEX;
import static org.junit.jupiter.api.Assertions.*;

class MatchTest {
    private final UUID validId = UUID.randomUUID();
    private final Instant now = Instant.now();
    
    private String randomTeamName() {
        return RandomStringUtils.randomAlphabetic(1, TEAM_NAME_LENGTH_LIMIT);
    }

    @Test
    void shouldCreateValidMatch() {
        int score = new Random().nextInt(0, 50);
        String home = randomTeamName();
        String away = randomTeamName();
        if (home.equals(away)) { return; }
        Match match = new Match(validId, home, away, score, score + 1, now);
        assertEquals(validId, match.id());
        assertEquals(home, match.homeTeam());
        assertEquals(away, match.awayTeam());
        assertEquals(score, match.homeScore());
        assertEquals(score + 1, match.awayScore());
        assertEquals(score + score + 1, match.totalScore());
        assertEquals(now, match.startTime());

        Match start = Match.start(home, away);
        assertNotEquals(match.id(), start.id());
        assertEquals(match.homeTeam(), start.homeTeam());
        assertEquals(match.awayTeam(), start.awayTeam());
        assertEquals(0, start.homeScore());
        assertEquals(0, start.awayScore());
        assertEquals(0, start.totalScore());

        score = new Random().nextInt(0, 50);
        Match update = Match.updateScore(match, score, score + 1);
        assertEquals(match.id(), update.id());
        assertEquals(match.homeTeam(), update.homeTeam());
        assertEquals(match.awayTeam(), update.awayTeam());
        assertEquals(score, update.homeScore());
        assertEquals(score + 1, update.awayScore());
        assertEquals(score + score + 1, update.totalScore());
        assertEquals(match.startTime(), update.startTime());
    }

    @Test
    void shouldThrowForNullFields() {
        assertThrows(NullPointerException.class, () -> new Match(null, randomTeamName(), randomTeamName(), 0, 0, now));
        assertThrows(NullPointerException.class, () -> new Match(validId, null, randomTeamName(), 0, 0, now));
        assertThrows(NullPointerException.class, () -> new Match(validId, randomTeamName(), null, 0, 0, now));
        assertThrows(NullPointerException.class, () -> new Match(validId, randomTeamName(), randomTeamName(), 0, 0, null));
    }

    @Test
    void shouldRejectInvalidTeamNames() {
        assertThrows(IllegalArgumentException.class, () -> new Match(validId, "Real$", "Real2$", 0, 0, now));
        assertThrows(IllegalArgumentException.class, () -> new Match(validId, "Team!", "Team2!", 0, 0, now));
        assertThrows(IllegalArgumentException.class, () -> new Match(validId, "", "", 0, 0, now));
        assertThrows(IllegalArgumentException.class, () -> new Match(validId, "A".repeat(31), "B".repeat(31), 0, 0, now));
    }

    @Test
    void shouldRejectOutOfBoundsScores() {
        int invalidScore = new Random().nextInt(51, 100);
        assertThrows(IllegalArgumentException.class, () -> new Match(validId, randomTeamName(), randomTeamName(), -1, 0, now));
        assertThrows(IllegalArgumentException.class, () -> new Match(validId, randomTeamName(), randomTeamName(), 0, -1, now));
        assertThrows(IllegalArgumentException.class, () -> new Match(validId, randomTeamName(), randomTeamName(), 0, 51, now));
        assertThrows(IllegalArgumentException.class, () -> new Match(validId, randomTeamName(), randomTeamName(), 51, 0, now));
        assertThrows(IllegalArgumentException.class, () -> new Match(validId, randomTeamName(), randomTeamName(), 100, 0, now));
        assertThrows(IllegalArgumentException.class, () -> new Match(validId, randomTeamName(), randomTeamName(), 0, 100, now));
        assertThrows(IllegalArgumentException.class, () -> new Match(validId, randomTeamName(), randomTeamName(), 0, invalidScore, now));
        assertThrows(IllegalArgumentException.class, () -> new Match(validId, randomTeamName(), randomTeamName(), invalidScore, 0, now));
    }

    private final Consumer<String> CREATE_MATCH_NEGATIVE_CASE = (name) -> {
        String validName = randomTeamName();
        String message = String.format("Wrong team name: %s", name);
        assertThrows(RuntimeException.class, () -> Match.start(validName, name), message);
        assertThrows(RuntimeException.class, () -> Match.start(name, validName), message);
    };

    @Test
    @DisplayName("scenarios with wrong symbols")
    void shouldUpdateScore() {
        CREATE_MATCH_NEGATIVE_CASE.accept("");
        CREATE_MATCH_NEGATIVE_CASE.accept("⚽");
        CREATE_MATCH_NEGATIVE_CASE.accept(null);
        CREATE_MATCH_NEGATIVE_CASE.accept(" ");
        CREATE_MATCH_NEGATIVE_CASE.accept("A".repeat(31));
        CREATE_MATCH_NEGATIVE_CASE.accept("A".repeat(32));
        CREATE_MATCH_NEGATIVE_CASE.accept(RandomStringUtils.randomAlphabetic(TEAM_NAME_LENGTH_LIMIT + 1, 100));
    }


    @Test
    @DisplayName("two identical team names")
    void bad_createMatch_same() {
        String same = randomTeamName();
        assertThrows(RuntimeException.class, () -> Match.start(same, same), "Same team name.");
        assertThrows(RuntimeException.class, () -> Match.start(same + " ", same), "Same team name.");
        assertThrows(RuntimeException.class, () -> Match.start(same + "  ", same), "Same team name.");
        assertThrows(RuntimeException.class, () -> Match.start(same, same + " "), "Same team name.");
        assertThrows(RuntimeException.class, () -> Match.start(same, same + "  "), "Same team name.");
    }

    @RepeatedTest(1_000)
    @DisplayName("scenarios with wrong random symbols")
    void bad_createMatch_random() {
        String invalidSymbol = RandomStringUtils.random(1);
        if (invalidSymbol.matches(TEAM_NAME_REGEX)) {
            return;
        }
        CREATE_MATCH_NEGATIVE_CASE.accept(invalidSymbol);
    }
}