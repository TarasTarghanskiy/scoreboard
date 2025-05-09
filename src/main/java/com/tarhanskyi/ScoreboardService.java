package com.tarhanskyi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScoreboardService {

    private final Map<UUID, Match> matches = new ConcurrentHashMap<>();

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

    public Match updateScore(UUID matchId, int homeScore, int awayScore) {
        Match match = matches.get(matchId);
        ScoreboardException.notFoundById(matchId, match == null);
        Match updated = Match.updateScore(match, homeScore, awayScore);
        matches.put(updated.id(), updated);
        return updated;
    }

    public void finishMatch(UUID matchId) {
        ScoreboardException.notFoundById(matchId, !matches.containsKey(matchId));
        matches.remove(matchId);
    }

    public List<Match> getSummary() {
        return matches.values().stream()
                .sorted(Comparator
                        .comparingInt(Match::totalScore)
                        .thenComparing(Match::startTime).reversed())
                .collect(Collectors.toList());
    }
}