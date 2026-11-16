package com.example.gamematch.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.gamematch.matching.MatchResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Temporary in-memory data source so you can test all 4 screens before Student A finishes Room.
public class FakeGameRepository implements GameRepository {
    private final List<Game> sampleGames = new ArrayList<>();
    private final Set<Integer> favoriteIds = new HashSet<>();
    private final MutableLiveData<List<Game>> favoritesLiveData = new MutableLiveData<>(new ArrayList<>());

    public FakeGameRepository() {
        sampleGames.add(new Game(1, "Outer Wilds",
                "Get lost. Find a little wonder.",
                Arrays.asList("PC / Windows", "PlayStation", "Nintendo Switch"),
                Arrays.asList("Relaxed", "Curious", "Adventurous"),
                Arrays.asList("Adventure"),
                "https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/753640/header.jpg",
                "https://store.steampowered.com/app/753640"));

        sampleGames.add(new Game(2, "Journey",
                "Take the scenic route through the sky.",
                Arrays.asList("PC / Windows", "PlayStation", "Mobile"),
                Arrays.asList("Curious", "Creative", "Relaxed"),
                Arrays.asList("Adventure"),
                "https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/638230/header.jpg",
                "https://store.steampowered.com/app/638230"));

        sampleGames.add(new Game(3, "Stardew Valley",
                "Build a slower life by the sea.",
                Arrays.asList("PC / Windows", "Nintendo Switch", "Mobile"),
                Arrays.asList("Relaxed", "Creative", "Nostalgic"),
                Arrays.asList("Simulation"),
                "https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/413150/header.jpg",
                "https://store.steampowered.com/app/413150"));
    }

    @Override
    public LiveData<List<MatchResult>> getRecommendations(List<String> selectedMoods, String platform) {
        MutableLiveData<List<MatchResult>> results = new MutableLiveData<>();
        List<MatchResult> matched = new ArrayList<>();

        // Return empty list if no moods were selected
        if (selectedMoods == null || selectedMoods.isEmpty()) {
            results.setValue(matched);
            return results;
        }

        for (Game game : sampleGames) {
            // Filter out games that don't support the chosen platform
            if (platform != null && !platform.equals("Any platform") && !game.platforms.contains(platform)) {
                continue;
            }

            // Check which selected moods match this game
            List<String> hits = new ArrayList<>();
            for (String mood : selectedMoods) {
                if (game.moods.contains(mood)) {
                    hits.add(mood);
                }
            }

            if (!hits.isEmpty()) {
                // Score formula: 100 * (matched selected moods / total selected moods)
                int score = (int) Math.round((100.0 * hits.size()) / selectedMoods.size());
                String explanation = "Matches your " + String.join(" and ", hits) + " preferences.";
                matched.add(new MatchResult(game, score, explanation));
            }
        }

        // Sort highest score first and keep top 5
        matched.sort((a, b) -> Integer.compare(b.score, a.score));
        results.setValue(matched.size() > 5 ? matched.subList(0, 5) : matched);
        return results;
    }

    @Override
    public LiveData<Game> getGameById(int gameId) {
        MutableLiveData<Game> data = new MutableLiveData<>();
        for (Game g : sampleGames) {
            if (g.id == gameId) {
                data.setValue(g);
                break;
            }
        }
        return data;
    }

    @Override
    public LiveData<List<Game>> getFavorites() {
        return favoritesLiveData;
    }

    @Override
    public LiveData<Boolean> isFavorite(int gameId) {
        return new MutableLiveData<>(favoriteIds.contains(gameId));
    }

    @Override
    public void addFavorite(int gameId) {
        favoriteIds.add(gameId);
        refreshFavorites();
    }

    @Override
    public void removeFavorite(int gameId) {
        favoriteIds.remove(gameId);
        refreshFavorites();
    }

    private void refreshFavorites() {
        List<Game> favs = new ArrayList<>();
        for (Game g : sampleGames) {
            if (favoriteIds.contains(g.id)) {
                favs.add(g);
            }
        }
        favoritesLiveData.setValue(favs);
    }
}