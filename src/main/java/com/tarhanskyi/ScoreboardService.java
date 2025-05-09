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
     * Throws an exception if the maximum number of matches is reached or
     * if either team is already participating in an active match.
     *
     * @param homeTeam the name of the home team
     * @param awayTeam the name of the away team
     * @return the newly created {@link Match}
     * @throws ScoreboardException if the match limit is reached or a team is already in a match
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
     * Throws an exception if the match is not found.
     *
     * @param matchId   the UUID of the match to update
     * @param homeScore the new score for the home team
     * @param awayScore the new score for the away team
     * @return the updated {@link Match}
     * @throws ScoreboardException if the match is not found
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
     * Throws an exception if the match is not found.
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