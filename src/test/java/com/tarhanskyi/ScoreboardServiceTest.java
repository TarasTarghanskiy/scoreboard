package com.tarhanskyi;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ScoreboardServiceTest {
    private static final int MATCHES_LIMIT = 100;
    private ScoreboardService service;
    /** Maximum allowed length for team names */
    private final int TEAM_NAME_LENGTH_LIMIT = 30;
    /** Team name pattern: letters, digits, spaces and special chars */
    private final String TEAM_NAME_SYMBOL_LIMIT = "^[\\p{L}\\d\\s\\-'.&(),/]$";
    /** Maximum allowed number for the team match scores */
    private final int SCORE_LIMIT = 50;

    @BeforeEach
    void setUp() {
        service = new ScoreboardService();
    }

    private String randomTeamName() {
        return RandomStringUtils.randomAlphabetic(1, TEAM_NAME_LENGTH_LIMIT);
    }

    private Match startRandomMatch() {
        String homeName = randomTeamName();
        String awayName = randomTeamName();
        if (homeName.equals(awayName)) { return startRandomMatch(); }
        return service.startMatch(homeName, awayName);
    }

    @Nested
    @DisplayName("Create match")
    class CreateMatch {

        @RepeatedTest(100)
        @DisplayName("basic scenario")
        void ok_createMatch() {
            // Arrange
            Instant now = Instant.now();
            String homeTeam = randomTeamName();
            String awayTeam = randomTeamName();
            if (homeTeam.equals(awayTeam)) { return; }

            // Act
            Match match = service.startMatch(homeTeam, awayTeam);

            // Assert
            assertNotNull(match, "Match should not be null");
            assertNotNull(match.id(), "Match ID should not be null");
            assertEquals(homeTeam, match.homeTeam(), "Home team should be the same as the one passed in");
            assertEquals(awayTeam, match.awayTeam(), "Away team should be the same as the one passed in");
            assertEquals(0, match.homeScore(), "Home score should be 0");
            assertEquals(0, match.awayScore(), "Away score should be 0");
            assertEquals(0, match.totalScore(), "Total score should be 0");
            assertTrue(match.startTime().isAfter(now), "Start time should be after now");
        }

        @Test
        @DisplayName("100 active games is the limit")
        void bad_createMatch_gamesLimit() {
            for (int i = 0; i < MATCHES_LIMIT; i++) {
                startRandomMatch();
            }
            assertEquals(MATCHES_LIMIT, service.getSummary().size(), "Summary should have 100 matches");
            assertThrows(RuntimeException.class,
                    ScoreboardServiceTest.this::startRandomMatch,
                    "Too many games started");
        }


        @Test
        @DisplayName("concurrency: multiple matches without exceptions or duplicates")
        void concurrent_startMatch_shouldNotThrowOrDuplicate() throws InterruptedException {
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            Set<String> names = ConcurrentHashMap.newKeySet();

            for (int i = 0; i < threadCount; i++) {
                int index = i;
                executor.submit(() -> {
                    try {
                        Match match = service.startMatch("Team" + index, "Opponent" + index);
                        names.add(match.homeTeam());
                    } catch (Exception e) {
                        fail("Exception during concurrent startMatch: " + e.getMessage());
                    }
                });
            }

            executor.shutdown();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS), "Executor did not finish in time");

            assertEquals(threadCount, names.size(), "All matches should be created uniquely");
        }

        private final Consumer<String> CREATE_MATCH_NEGATIVE_CASE = (name) -> {
            String validName = randomTeamName();
            String message = String.format("Wrong team name: %s", name);
            assertThrows(RuntimeException.class, () -> service.startMatch(name, validName), message);
            assertThrows(RuntimeException.class, () -> service.startMatch(validName, name), message);
        };

        @Test
        @DisplayName("scenarios with wrong symbols")
        void bad_createMatch() {
            CREATE_MATCH_NEGATIVE_CASE.accept("");
            CREATE_MATCH_NEGATIVE_CASE.accept("⚽");
            CREATE_MATCH_NEGATIVE_CASE.accept(null);
            CREATE_MATCH_NEGATIVE_CASE.accept(" ");
            CREATE_MATCH_NEGATIVE_CASE.accept(randomTeamName() + " ");
            CREATE_MATCH_NEGATIVE_CASE.accept(randomTeamName() + "  ");
            CREATE_MATCH_NEGATIVE_CASE.accept(" " + randomTeamName());
            CREATE_MATCH_NEGATIVE_CASE.accept("  " + randomTeamName());
            CREATE_MATCH_NEGATIVE_CASE.accept(randomTeamName() + randomTeamName());
            CREATE_MATCH_NEGATIVE_CASE.accept(RandomStringUtils.randomAlphabetic(TEAM_NAME_LENGTH_LIMIT + 1, MAX_VALUE));
        }

        @RepeatedTest(1_000)
        @DisplayName("scenarios with wrong random symbols")
        void bad_createMatch_random() {
            String invalidSymbol = RandomStringUtils.random(1);
            if (invalidSymbol.matches(TEAM_NAME_SYMBOL_LIMIT)) {
                return;
            }
            CREATE_MATCH_NEGATIVE_CASE.accept(invalidSymbol);
        }

        @Test
        @DisplayName("two identical team names")
        void bad_createMatch_same() {
            String same = randomTeamName();
            assertThrows(RuntimeException.class, () -> service.startMatch(same, same), "Same team name.");
        }

        @RepeatedTest(10)
        @DisplayName("teams already in the game")
        void bad_createMatch_forTeamInGame() {
            Match match = startRandomMatch();
            CREATE_MATCH_NEGATIVE_CASE.accept(match.homeTeam());
            CREATE_MATCH_NEGATIVE_CASE.accept(match.awayTeam());
        }
    }

    @Nested
    @DisplayName("Update score")
    class UpdateScore {
        @RepeatedTest(100)
        @DisplayName("basic scenario")
        void ok_updateScore() {
            // Arrange
            int homeScore = new Random().nextInt(0, SCORE_LIMIT);
            int awayScore = new Random().nextInt(0, SCORE_LIMIT);
            Match expected =  startRandomMatch();

            // Act
            Match actual = service.updateScore(expected.id(), homeScore, awayScore);

            // Assert
            assertNotNull(actual, "Match should not be null");
            assertNotNull(actual.id(), "Match ID should not be null");
            assertEquals(expected.id(), actual.id(), "Match ID should not be null");
            assertEquals(expected.homeTeam(), actual.homeTeam(), "team should be the same as the one passed in");
            assertEquals(expected.awayTeam(), actual.awayTeam(), "team should be the same as the one passed in");
            assertEquals(expected.homeScore(), homeScore, "Home score should match expected value");
            assertEquals(expected.awayScore(), awayScore, "Home score should match expected value");
            assertEquals(expected.totalScore(), homeScore + awayScore, "Total score should be 0");
            assertEquals(expected.startTime(), actual.startTime(), "Start time should be after now");
        }

        @Test
        @DisplayName("concurrency: should handle score updates without data corruption")
        void concurrent_updateScore_shouldNotCorruptData() throws Exception {
            Match match = startRandomMatch();

            Runnable updateTask = () -> {
                for (int i = 0; i < 10; i++) {
                    service.updateScore(match.id(), i, i);
                }
            };

            Thread t1 = new Thread(updateTask);
            Thread t2 = new Thread(updateTask);

            t1.start();
            t2.start();
            t1.join();
            t2.join();

            Match result = service.getSummary().getFirst();
            assertEquals(9, result.homeScore(), "Final score should reflect multiple updates");
        }



        @Test
        @DisplayName("same input repeatedly")
        void ok_updateScore_sameInputRepeatedly() {
            UUID id = startRandomMatch().id();
            service.updateScore(id, 2, 2);
            Match match1 = service.updateScore(id, 2, 2);
            Match match2 = service.updateScore(id, 2, 2);
            assertEquals(match1, match2, "Updates with same scores should return the same result");
        }

        @Test
        @DisplayName("can't update finished match")
        void bad_updateScore_afterFinish() {
            Match match = startRandomMatch();
            service.updateScore(match.id(), 1, 1);
            service.finishMatch(match.id());
            assertThrows(RuntimeException.class, () -> service.updateScore(match.id(), 1, 1));
        }

        @Test
        @DisplayName("valid score inputs")
        void ok_updateScore_validNumbers() {
            UUID validID =  startRandomMatch().id();

            for (int i = 0; i < SCORE_LIMIT + 1; i++) {
                Match match = service.updateScore(validID, i, i);
                assertEquals(i + i, match.totalScore(), "Total score should be as expected");
                assertEquals(i, match.homeScore(), "Home score should be as expected");
                assertEquals(i, match.awayScore(), "Away score should be as expected");
            }
        }

        private final BiConsumer<UUID, Integer> UPDATE_SCORE_NEGATIVE_CASE = (id, score) -> {
            String message = String.format("Update score wrong params: %s, %s", id, score);
            assertThrows(RuntimeException.class, () -> service.updateScore(id, score, 0), message);
            assertThrows(RuntimeException.class, () -> service.updateScore(id, 0, score), message);
        };

        @Test
        @DisplayName("invalid score inputs")
        void bad_updateScore() {
            UUID validID = startRandomMatch().id();
            UPDATE_SCORE_NEGATIVE_CASE.accept(UUID.randomUUID(), 0);
            UPDATE_SCORE_NEGATIVE_CASE.accept(null, 0);
            UPDATE_SCORE_NEGATIVE_CASE.accept(validID, -2);
            UPDATE_SCORE_NEGATIVE_CASE.accept(validID, -1);
            UPDATE_SCORE_NEGATIVE_CASE.accept(validID, SCORE_LIMIT + 1);
            UPDATE_SCORE_NEGATIVE_CASE.accept(validID, SCORE_LIMIT + 2);
        }
    }

    @Nested
    @DisplayName("Finish match")
    class FinishMatch {
        @RepeatedTest(10)
        @DisplayName("basic scenario")
        void ok_finishMatch() {
            // Arrange
            Match expected =  startRandomMatch();
            // Act
            service.finishMatch(expected.id());
            // Assert
            assertEquals(0, service.getSummary().size(), "Summary should be empty");
        }

        @RepeatedTest(10)
        @DisplayName("invalid id inputs")
        void bad_finishMatch() {
            assertThrows(RuntimeException.class, () -> service.finishMatch(UUID.randomUUID()), "Match not found");
            assertThrows(RuntimeException.class, () -> service.finishMatch(null), "ID is null");
        }

        @Test
        @DisplayName("can't finish twice")
        void bad_finishMatch_trice() {
            Match match = startRandomMatch();
            service.finishMatch(match.id());
            assertThrows(RuntimeException.class, () -> service.finishMatch(match.id()));
        }
    }

    @Nested
    @DisplayName("Summary")
    class Summary {
        @RepeatedTest(10)
        @DisplayName("valid matches in the list")
        void ok_summary() {
            List<Match> expectedList = new ArrayList<>();
            for (int i = 0; i < MATCHES_LIMIT; i++) {
                assertEquals(i, service.getSummary().size());
                expectedList.add(startRandomMatch());

                assertEquals(expectedList.size(), service.getSummary().size(), "size should be as expected");
                expectedList.forEach(match -> assertTrue(service.getSummary().contains(match), "Summary should contain all matches"));
            }
        }

        @Test
        @DisplayName("empty at the beginning")
        void ok_summary_empty() {
            assertEquals(0, service.getSummary().size(), "Summary should be empty");
        }

        @Test
        @DisplayName("sort by higher total score")
        void ok_summary_orderTotal() {
            UUID firstID = startRandomMatch().id();
            UUID secondID = startRandomMatch().id();

            for (int i = 0; i < SCORE_LIMIT + 1; i++) {
                UUID temp = firstID;
                firstID = secondID;
                secondID = temp;

                List<Match> summary = service.getSummary();
                assertEquals(2, summary.size(), "Summary should have 2 matches");
                assertEquals(firstID, summary.getFirst().id(), "First match should be the one started first");
                assertEquals(secondID, summary.getLast().id(), "Second match should be the one started last");

                service.updateScore(secondID, i, i);
            }
        }

        @Test
        @DisplayName("sort by higher Instant if equal total score")
        void ok_summary_orderInstant() {
            List<Match> matches = new ArrayList<>();
            for (int i = 0; i < SCORE_LIMIT + 1; i++) {
                List<Match> actual = service.getSummary();
                List<Match> expected = new ArrayList<>(matches);
                Collections.reverse(expected);
                assertEquals(expected.size(), actual.size(), "Summary should have same matches");
                assertIterableEquals(
                        expected.stream().sorted().toList(),
                        service.getSummary().stream().sorted().toList(),
                        "Summary should be as expected"
                );
                matches.add(startRandomMatch());
            }
        }

        @Test
        @DisplayName("response should be immutable")
        void ok_summary_immutable() {
            // Arrange
            Match match = startRandomMatch();

            // Act
            List<Match> summary = service.getSummary();

            // Assert
            // 1. Try to modify the list itself
            assertThrows(UnsupportedOperationException.class, () -> summary.add(match));
            assertThrows(UnsupportedOperationException.class, summary::removeFirst);
            assertThrows(UnsupportedOperationException.class, summary::clear);

            // 2. Verify that original data wasn't affected
            List<Match> newSummary = service.getSummary();
            assertEquals(1, newSummary.size(), "Original data should remain unchanged");
            assertEquals(match, newSummary.getFirst(), "Original data should remain unchanged");

            // 3. Verify that getting summary multiple times returns different list instances
            List<Match> anotherSummary = service.getSummary();
            assertNotSame(summary, anotherSummary, "Should return new list instance each time");

        }
    }

}