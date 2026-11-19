package com.example.gamematch.domain;

import com.example.gamematch.data.Game;
import com.example.gamematch.matching.MatchResult;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour MatchingEngine.
 * Aucune dépendance Android → s'exécute avec ./gradlew test (JUnit pur).
 */
public class MatchingEngineTest {

    private MatchingEngine engine;
    private List<Game> catalog;

    @Before
    public void setUp() {
        engine = new MatchingEngine();

        // Catalogue minimal de test
        Game g1 = new Game(1, "GameA", "Desc A",
                Arrays.asList("PC / Windows", "PlayStation"),
                Arrays.asList("Relaxed", "Curious"),
                null, null, null);

        Game g2 = new Game(2, "GameB", "Desc B",
                Arrays.asList("Xbox", "Nintendo Switch"),
                Arrays.asList("Competitive", "Energetic"),
                null, null, null);

        Game g3 = new Game(3, "GameC", "Desc C",
                Arrays.asList("PC / Windows", "Mobile"),
                Arrays.asList("Curious", "Creative", "Relaxed"),
                null, null, null);

        catalog = Arrays.asList(g1, g2, g3);
    }

    // Test 1 : liste vide si aucun mood sélectionné
    @Test
    public void emptyMoods_returnsEmptyList() {
        List<MatchResult> results = engine.match(catalog, Collections.emptyList(), "Any platform");
        assertTrue(results.isEmpty());
    }

    // Test 2 : null moods → liste vide sans crash
    @Test
    public void nullMoods_returnsEmptyList() {
        List<MatchResult> results = engine.match(catalog, null, "Any platform");
        assertTrue(results.isEmpty());
    }

    // Test 3 : null catalogue → liste vide sans crash
    @Test
    public void nullCatalog_returnsEmptyList() {
        List<MatchResult> results = engine.match(null, Arrays.asList("Relaxed"), "Any platform");
        assertTrue(results.isEmpty());
    }

    // Test 4 : score 100% si tous les moods correspondent
    @Test
    public void fullMatch_returns100Score() {
        List<MatchResult> results = engine.match(catalog,
                Arrays.asList("Relaxed", "Curious"), MatchingEngine.PLATFORM_ALL);
        // GameA et GameC ont les 2 moods
        boolean hasFullMatch = results.stream().anyMatch(r -> r.score == 100);
        assertTrue(hasFullMatch);
    }

    // Test 5 : score 50% si 1 mood sur 2 correspond
    @Test
    public void partialMatch_returns50Score() {
        List<MatchResult> results = engine.match(catalog,
                Arrays.asList("Relaxed", "Competitive"), MatchingEngine.PLATFORM_ALL);
        // GameA a "Relaxed" mais pas "Competitive" → 50%
        // GameB a "Competitive" mais pas "Relaxed" → 50%
        boolean has50 = results.stream().anyMatch(r -> r.score == 50);
        assertTrue(has50);
    }

    // Test 6 : filtre plateforme exclut les jeux hors plateforme
    @Test
    public void platformFilter_excludesOtherPlatforms() {
        List<MatchResult> results = engine.match(catalog,
                Arrays.asList("Competitive"), "Xbox");
        // Seul GameB est sur Xbox avec le mood Competitive
        assertEquals(1, results.size());
        assertEquals("GameB", results.get(0).game.title);
    }

    // Test 7 : "Any platform" ne filtre rien
    @Test
    public void anyPlatform_includesAllGames() {
        List<MatchResult> results = engine.match(catalog,
                Arrays.asList("Curious"), MatchingEngine.PLATFORM_ALL);
        // GameA et GameC ont "Curious"
        assertEquals(2, results.size());
    }

    // Test 8 : résultats triés par score décroissant
    @Test
    public void results_sortedByScoreDescending() {
        List<MatchResult> results = engine.match(catalog,
                Arrays.asList("Relaxed", "Curious", "Creative"), MatchingEngine.PLATFORM_ALL);
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).score >= results.get(i + 1).score);
        }
    }

    // Test 9 : maximum 5 résultats retournés
    @Test
    public void results_limitedToFive() {
        // Catalogue de 10 jeux avec le même mood
        List<Game> bigCatalog = Arrays.asList(
                makeGame(10, "Relaxed"),
                makeGame(11, "Relaxed"),
                makeGame(12, "Relaxed"),
                makeGame(13, "Relaxed"),
                makeGame(14, "Relaxed"),
                makeGame(15, "Relaxed"),
                makeGame(16, "Relaxed"),
                makeGame(17, "Relaxed")
        );
        List<MatchResult> results = engine.match(bigCatalog,
                Arrays.asList("Relaxed"), MatchingEngine.PLATFORM_ALL);
        assertTrue(results.size() <= 5);
    }

    // Helper pour créer un jeu de test rapide
    private Game makeGame(int id, String... moods) {
        Game g = new Game();
        g.id = id;
        g.title = "Game" + id;
        g.platforms = Arrays.asList("PC / Windows");
        g.moods = Arrays.asList(moods);
        return g;
    }
}
