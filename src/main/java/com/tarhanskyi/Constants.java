package com.tarhanskyi;

/**
 * Central hub for all constants used in the Match-related functionality.
 * This includes validation rules, limits, and patterns for team names and scores.
 */
final class Constants {
    private Constants() { }
    /**
     * Maximum number of concurrent matches allowed in the system
     */
    static final int MATCHES_LIMIT = 100;

    /**
     * Maximum allowed length for team names
     */
    static final int TEAM_NAME_LENGTH_LIMIT = 30;

    /**
     * Maximum allowed score for any team in a match
     */
    static final int SCORE_LIMIT = 50;

    /**
     * Regular expression pattern for valid team names.
     * Allows:
     * - Letters (any Unicode letter)
     * - Digits
     * - Spaces
     * - Special characters: hyphen, apostrophe, period, ampersand, parentheses, comma, forward slash
     */
    static final String TEAM_NAME_REGEX = String.format("^[\\p{L}\\d\\s-'.&(),/]{1,%s}$", TEAM_NAME_LENGTH_LIMIT);

    /**
     * Error message templates for validation failures
     */
    static final class ErrorMessages {
        private ErrorMessages() { }
        public static final String HOME_TEAM_NULL = "Home team name must not be null";
        public static final String AWAY_TEAM_NULL = "Away team name must not be null";
        public static final String START_TIME_NULL = "Start time must not be null";

        public static final String MATCH_ALREADY_EXISTS = "Match already exists for a team: %s";
        public static final String MATCH_NOT_FOUND = "Match not found with ID: %s";
        public static final String TOO_MANY_ACTIVE = "Too many active matches, current: %s, limit: %s";
        public static final String MATCH_ID = "Match ID must not be null";
        public static final String SAME_TEAMS = "Team names must be different: %s";

        public static final String INVALID_HOME_TEAM = "Invalid home team name: %s";
        public static final String INVALID_AWAY_TEAM = "Invalid away team name: %s";

        public static final String HOME_SCORE_RANGE = "Home score out of range: %d";
        public static final String AWAY_SCORE_RANGE = "Away score out of range: %d";
    }

}
