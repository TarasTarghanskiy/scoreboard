package com.tarhanskyi;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;


public record Match(
        UUID id,
        String homeTeam,
        String awayTeam,
        int homeScore,
        int awayScore,
        Instant startTime
) {
    private static final int MAX_SCORE = 50;
    private static final String TEAM_NAME_REGEX = "^[\\p{L}\\d\\s-'.&(),/]{1,30}$";

    public Match {
        Objects.requireNonNull(id, "Match ID must not be null");
        Objects.requireNonNull(homeTeam, "Home team name must not be null");
        Objects.requireNonNull(awayTeam, "Away team name must not be null");
        Objects.requireNonNull(startTime, "Start time must not be null");


        if (homeTeam.trim().equals(awayTeam.trim())) {
            throw new IllegalArgumentException("Team names are the same: " + homeTeam + " and " + awayTeam);
        }

        if (!homeTeam.trim().matches(TEAM_NAME_REGEX)) {
            throw new IllegalArgumentException("Invalid home team name: " + homeTeam);
        }
        if (!awayTeam.trim().matches(TEAM_NAME_REGEX)) {
            throw new IllegalArgumentException("Invalid away team name: " + awayTeam);
        }
        if (homeScore < 0 || homeScore > MAX_SCORE) {
            throw new IllegalArgumentException("Home score out of range: " + homeScore);
        }
        if (awayScore < 0 || awayScore > MAX_SCORE) {
            throw new IllegalArgumentException("Away score out of range: " + awayScore);
        }
    }

    int totalScore() {
        return homeScore + awayScore;
    }

    static Match start(String homeTeam, String awayTeam) {
        return new Match(UUID.randomUUID(), homeTeam, awayTeam, 0, 0, Instant.now());
    }

    static Match updateScore(Match match, int homeScore, int awayScore) {
        return new Match(
                match.id(),
                match.homeTeam(),
                match.awayTeam(),
                homeScore,
                awayScore,
                match.startTime()
        );
    }
}
