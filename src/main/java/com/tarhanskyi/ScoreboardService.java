package com.tarhanskyi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service responsible for managing football matches on a scoreboard.
 * Provides functionality to start matches, update scores, finish matches,
 * and generate a sorted summary of ongoing matches.
 *
 * @author Taras Tarhasnkyi
 */
public class ScoreboardService {

    private final Map<UUID, Match> matches = new ConcurrentHashMap<>();

    /**
     * Starts a new match with the given home and away teams.
     *
     * @param homeTeam the name of the home team (non-null, must match {@code TEAM_NAME_REGEX}, typically letters, numbers, spaces, or hyphens)
     * @param awayTeam the name of the away team (non-null, must match {@code TEAM_NAME_REGEX}, typically letters, numbers, spaces, or hyphens)
     * @return the newly created {@link Match}
     * @throws ScoreboardException      if the match limit is reached or a team is already in a match
     * @throws NullPointerException     if any required field is null
     * @throws IllegalArgumentException if team names are invalid, identical, or scores are out of bounds
     */
    public Match startMatch(String homeTeam, String awayTeam) {
        ScoreboardException.matchLimitReached(matches.size());
        matches.values().stream()
                .flatMap(match -> Stream.of(match.homeTeam(), match.awayTeam()))
                .filter(team -> team.equals(homeTeam) || team.equals(awayTeam))
                .findAny()
                .ifPresent(ScoreboardException::matchExistsFor);
        Match match = Match.start(homeTeam, awayTeam);
        matches.put(match.id(), match);
        return match;
    }

    /**
     * Updates the score for a given match by ID.
     *
     * @param matchId   the UUID of the match to update
     * @param homeScore the new score for the home team (must be between 0 and {@code SCORE_LIMIT})
     * @param awayScore the new score for the away team (must be between 0 and {@code SCORE_LIMIT})
     * @return the updated {@link Match}
     * @throws ScoreboardException      if the match is not found
     * @throws IllegalArgumentException if scores are out of bounds
     */
    public Match updateScore(UUID matchId, int homeScore, int awayScore) {
        Match match = matches.get(matchId);
        ScoreboardException.notFoundById(matchId, match == null);
        Match updated = Match.updateScore(match, homeScore, awayScore);
        matches.put(updated.id(), updated);
        return updated;
    }

    /**
     * Finishes and removes the match with the given ID from the scoreboard.
     *
     * @param matchId the UUID of the match to finish
     * @throws ScoreboardException if the match is not found
     */
    public void finishMatch(UUID matchId) {
        ScoreboardException.notFoundById(matchId, !matches.containsKey(matchId));
        matches.remove(matchId);
    }

    /**
     * Returns a summary list of ongoing matches sorted by:
     * <ul>
     *     <li>Total score (bigger first) </li>
     *     <li>Start time (the most recent first) </li>
     * </ul>
     *
     * @return a sorted list of ongoing {@link Match} instances
     */
    public List<Match> getSummary() {
        return matches.values().stream()
                .sorted(Comparator
                        .comparingInt(Match::totalScore)
                        .thenComparing(Match::startTime).reversed())
                .collect(Collectors.toList());
    }
}