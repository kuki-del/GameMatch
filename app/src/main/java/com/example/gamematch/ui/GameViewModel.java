package com.example.gamematch.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import com.example.gamematch.data.FakeGameRepository;
import com.example.gamematch.data.Game;
import com.example.gamematch.data.GameRepository;
import com.example.gamematch.matching.MatchResult;
import java.util.ArrayList;
import java.util.List;

// Holds UI state across all 4 screens and survives screen rotation.
public class GameViewModel extends ViewModel {
    private final GameRepository repository;

    // Stores the user's latest mood + platform search
    private final MutableLiveData<FilterParams> currentFilter = new MutableLiveData<>();
    // Stores the ID of the game the user tapped to inspect
    private final MutableLiveData<Integer> selectedGameId = new MutableLiveData<>();

    public GameViewModel() {
        // Uses fake data for now. When Student A finishes Room, you only swap this line.
        this.repository = new FakeGameRepository();
    }

    // Called when the user clicks "Find Games" on the Mood screen
    public void searchGames(List<String> moods, String platform) {
        currentFilter.setValue(new FilterParams(new ArrayList<>(moods), platform));
    }

    // Automatically fetches matching games whenever currentFilter updates
    public LiveData<List<MatchResult>> getRecommendations() {
        return Transformations.switchMap(currentFilter,
                filter -> repository.getRecommendations(filter.moods, filter.platform));
    }

    // Called when the user taps a game card in Results or Favorites
    public void selectGame(int gameId) {
        selectedGameId.setValue(gameId);
    }

    // Automatically fetches full game details whenever selectedGameId updates
    public LiveData<Game> getSelectedGame() {
        return Transformations.switchMap(selectedGameId, repository::getGameById);
    }

    public LiveData<List<Game>> getFavorites() {
        return repository.getFavorites();
    }

    public LiveData<Boolean> isFavorite(int gameId) {
        return repository.isFavorite(gameId);
    }

    // Adds or removes a favorite depending on its current saved status
    public void toggleFavorite(int gameId, boolean currentlyFavorite) {
        if (currentlyFavorite) {
            repository.removeFavorite(gameId);
        } else {
            repository.addFavorite(gameId);
        }
    }

    // Helper class to bundle moods and platform into one LiveData object
    private static class FilterParams {
        final List<String> moods;
        final String platform;

        FilterParams(List<String> moods, String platform) {
            this.moods = moods;
            this.platform = platform;
        }
    }

    // Permet à ResultsFragment d'afficher les ambiances choisies dans la barre de filtres
    public List<String> getCurrentMoods() {
        FilterParams params = currentFilter.getValue();
        return params != null ? params.moods : new ArrayList<>();
    }

    // Permet à ResultsFragment d'afficher la plateforme choisie
    public String getCurrentPlatform() {
        FilterParams params = currentFilter.getValue();
        return params != null ? params.platform : "Any platform";
    }

    // Holds the MatchResult score/explanation when opened from the Results screen (null if opened from Favorites)
    private final MutableLiveData<MatchResult> selectedMatchResult = new MutableLiveData<>();

    // Called when tapping a game card in ResultsFragment (keeps score + explanation)
    public void selectMatchResult(MatchResult matchResult) {
        selectedMatchResult.setValue(matchResult);
        if (matchResult != null && matchResult.game != null) {
            selectedGameId.setValue(matchResult.game.id);
        }
    }

    // Called when tapping a game card in FavoritesFragment (no search score to show)
    public void selectGameFromFavorites(int gameId) {
        selectedMatchResult.setValue(null);
        selectedGameId.setValue(gameId);
    }

    public LiveData<MatchResult> getSelectedMatchResult() {
        return selectedMatchResult;
    }
}