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

}
