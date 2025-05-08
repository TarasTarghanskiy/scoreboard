package com.tarhanskyi;


// Keep it simple

// Do not forget about edge cases!

import java.util.Collections;
import java.util.List;
import java.util.UUID;

// Just a simple library implementation.
// Focus on Quality.
// Use Test-Driven Development (TDD)
// pay attention to: OO design, Clean Code and adherence to SOLID principles.
// Please share your solution with a link to a source control repository (e.g. GitHub, GitLab, BitBucket)
// commit history is important
public class ScoreboardService {
    // 1. Start a new match, assuming initial score 0 – 0 and adding it the scoreboard. (Home team, away team)
    Match startMatch(String homeTeam, String awayTeam) {
        return Match.start(homeTeam, awayTeam);
    }

    //2. Update score. This should receive a pair of absolute scores: home team score and away team score. (id?)
    void updateScore(UUID matchId, int homeScore, int awayScore) {

    }

    //  3. Finish match currently in progress. This removes a match from the scoreboard.
    void finishMatch(UUID matchId) {

    }

    //4. Get a summary of matches in progress ordered by their total score. The matches with the
//same total score will be returned ordered by the most recently started match in the
//scoreboard.
    List<Match> getSummary() {
        return Collections.emptyList();
    }
}
