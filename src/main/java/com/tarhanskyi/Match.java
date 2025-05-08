package com.tarhanskyi;

import java.time.Instant;
import java.util.UUID;


public record Match(
        UUID id,
        String homeTeam,
        String awayTeam,
        int homeScore,
        int awayScore,
        Instant startTime
) {
    int totalScore() {
        return homeScore + awayScore;
    }

    public static Match start(String homeTeam, String awayTeam) {
        return new Match(UUID.randomUUID(), homeTeam, awayTeam, 0, 0, Instant.now());
    }

    public static Match updateScore(Match match, int homeScore, int awayScore) {
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
