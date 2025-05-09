package com.tarhanskyi;

import java.util.UUID;

import static com.tarhanskyi.Constants.ErrorMessages.MATCH_ALREADY_EXISTS;
import static com.tarhanskyi.Constants.ErrorMessages.MATCH_NOT_FOUND;
import static com.tarhanskyi.Constants.ErrorMessages.TOO_MANY_ACTIVE;
import static com.tarhanskyi.Constants.MATCHES_LIMIT;

public class ScoreboardException extends RuntimeException {
    private ScoreboardException(String message) {
        super(message);
    }

    static void matchExistsFor(String teamName) {
        throw new ScoreboardException(String.format(MATCH_ALREADY_EXISTS, teamName));
    }

    static void notFoundById(UUID id, boolean isNotFound) {
        if (isNotFound) throw new ScoreboardException(String.format(MATCH_NOT_FOUND, id));
    }

    static void matchLimitReached(int activeMatchesCount) {
        if (activeMatchesCount >= MATCHES_LIMIT)
            throw new ScoreboardException(String.format(TOO_MANY_ACTIVE, activeMatchesCount, MATCHES_LIMIT));
    }
}
