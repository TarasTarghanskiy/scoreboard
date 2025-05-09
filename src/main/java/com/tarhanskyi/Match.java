package com.tarhanskyi;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static com.tarhanskyi.Constants.ErrorMessages;
import static com.tarhanskyi.Constants.SCORE_LIMIT;
import static com.tarhanskyi.Constants.TEAM_NAME_REGEX;

/**
 * Immutable representation of a football match, including participating teams,
 * their scores, match ID, and start time.
 * <p>
 * This record enforces validation rules on construction, such as score limits,
 * unique team names, and valid formatting for team names.
 */
public record Match(
        UUID id,
        String homeTeam,
        String awayTeam,
        int homeScore,
        int awayScore,
        Instant startTime
) {
    /**
     * Constructs a new {@code Match} instance and validates its parameters.
     *
     * @param id         the unique identifier for the match
     * @param homeTeam   the name of the home team (non-null, valid format)
     * @param awayTeam   the name of the away team (non-null, valid format)
     * @param homeScore  the home team's score (must be between 0 and {@code SCORE_LIMIT})
     * @param awayScore  the away team's score (must be between 0 and {@code SCORE_LIMIT})
     * @param startTime  the start time of the match (non-null)
     * @throws NullPointerException     if any required field is null
     * @throws IllegalArgumentException if team names are invalid, identical, or scores are out of bounds
     */
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

    /**
     * Calculates the total score of the match.
     *
     * @return the sum of the home and away team scores
     */
    public int totalScore() {
        return homeScore + awayScore;
    }

    /**
     * Factory method for starting a new match.
     * Initializes scores to zero and start time to {@link Instant#now()}.
     *
     * @param homeTeam the name of the home team
     * @param awayTeam the name of the away team
     * @return a new {@code Match} instance
     */
    static Match start(String homeTeam, String awayTeam) {
        return new Match(UUID.randomUUID(), homeTeam, awayTeam, 0, 0, Instant.now());
    }

    /**
     * Factory method for updating the score of an existing match.
     * Returns a new {@code Match} instance with updated scores.
     *
     * @param match      the original match to update
     * @param homeScore  the new score for the home team
     * @param awayScore  the new score for the away team
     * @return a new {@code Match} instance with updated scores
     */
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
