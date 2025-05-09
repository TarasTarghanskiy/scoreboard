package com.tarhanskyi;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static com.tarhanskyi.Constants.ErrorMessages;
import static com.tarhanskyi.Constants.SCORE_LIMIT;
import static com.tarhanskyi.Constants.TEAM_NAME_REGEX;


public record Match(
        UUID id,
        String homeTeam,
        String awayTeam,
        int homeScore,
        int awayScore,
        Instant startTime
) {
    public Match {
        Objects.requireNonNull(id, ErrorMessages.MATCH_ID);
        Objects.requireNonNull(homeTeam, ErrorMessages.HOME_TEAM_NULL);
        Objects.requireNonNull(awayTeam, ErrorMessages.AWAY_TEAM_NULL);
        Objects.requireNonNull(startTime, ErrorMessages.START_TIME_NULL);

        String homeTrimmed = homeTeam.trim();
        String awayTrimmed = awayTeam.trim();

        if (homeTrimmed.equals(awayTrimmed)) {
            throw new IllegalArgumentException(String.format(ErrorMessages.SAME_TEAMS, homeTeam));
        }

        if (!homeTrimmed.matches(TEAM_NAME_REGEX)) {
            throw new IllegalArgumentException(String.format(ErrorMessages.INVALID_HOME_TEAM, homeTeam));
        }

        if (!awayTrimmed.matches(TEAM_NAME_REGEX)) {
            throw new IllegalArgumentException(String.format(ErrorMessages.INVALID_AWAY_TEAM, awayTeam));
        }

        if (homeScore < 0 || homeScore > SCORE_LIMIT) {
            throw new IllegalArgumentException(String.format(ErrorMessages.HOME_SCORE_RANGE, homeScore));
        }

        if (awayScore < 0 || awayScore > SCORE_LIMIT) {
            throw new IllegalArgumentException(String.format(ErrorMessages.AWAY_SCORE_RANGE, awayScore));
        }
    }

    public int totalScore() {
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
