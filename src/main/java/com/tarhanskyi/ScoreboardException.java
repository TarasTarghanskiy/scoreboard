package com.tarhanskyi;

import java.util.UUID;

public class ScoreboardException extends RuntimeException {
    private ScoreboardException(String message) {
        super(message);
    }

    static void matchExistsFor(String teamName) {
        throw new ScoreboardException("Match already exists for a team: " + teamName);
    }

    static void notFoundById(UUID id, boolean isNotFound) {
        if (isNotFound) throw new ScoreboardException("Match not found with ID: " + id);
    }

    static void matchLimitReached(int activeMatchesCount) {
        if (activeMatchesCount == 100)
            throw new ScoreboardException("Too many active matches, current: " + activeMatchesCount + ", limit: 100");
    }
}
