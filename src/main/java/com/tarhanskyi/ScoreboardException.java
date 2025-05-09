package com.tarhanskyi;

import java.util.UUID;

import static com.tarhanskyi.Constants.ErrorMessages.MATCH_ALREADY_EXISTS;
import static com.tarhanskyi.Constants.ErrorMessages.MATCH_NOT_FOUND;
import static com.tarhanskyi.Constants.ErrorMessages.TOO_MANY_ACTIVE;
import static com.tarhanskyi.Constants.MATCHES_LIMIT;

/**
 * Custom unchecked exception used for business rule enforcement in the scoreboard system.
 * This class provides static factory-style methods to throw exceptions for specific scenarios.
 *
 * <p>This exception should not be instantiated directly from outside the package.
 * It is used internally to signal domain-specific errors such as:
 * <ul>
 *     <li>Trying to start a match that conflicts with an existing team</li>
 *     <li>Referencing a match by an unknown ID</li>
 *     <li>Exceeding the maximum number of allowed active matches</li>
 * </ul>
 */
public class ScoreboardException extends RuntimeException {

    /**
     * Constructs a new {@code ScoreboardException} with the specified message.
     * Constructor is private to restrict creation to static factory methods.
     *
     * @param message the detailed error message
     */
    private ScoreboardException(String message) {
        super(message);
    }

    /**
     * Throws a {@code ScoreboardException} if a match already exists for the given team.
     *
     * @param teamName the name of the team already involved in another match
     * @throws ScoreboardException always
     */
    static void matchExistsFor(String teamName) {
        throw new ScoreboardException(String.format(MATCH_ALREADY_EXISTS, teamName));
    }

    /**
     * Throws a {@code ScoreboardException} if a match with the specified ID is not found.
     *
     * @param id         the match ID to check
     * @param isNotFound whether the match was not found (true to throw)
     * @throws ScoreboardException if {@code isNotFound} is true
     */
    static void notFoundById(UUID id, boolean isNotFound) {
        if (isNotFound) {
            throw new ScoreboardException(String.format(MATCH_NOT_FOUND, id));
        }
    }

    /**
     * Throws a {@code ScoreboardException} if the number of active matches exceeds the limit.
     *
     * @param activeMatchesCount the current count of active matches
     * @throws ScoreboardException if the active match count is too high
     */
    static void matchLimitReached(int activeMatchesCount) {
        if (activeMatchesCount >= MATCHES_LIMIT) {
            throw new ScoreboardException(String.format(TOO_MANY_ACTIVE, activeMatchesCount, MATCHES_LIMIT));
        }
    }
}
