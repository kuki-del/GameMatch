package com.example.gamematch.ui;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.gamematch.data.Game;
import com.example.gamematch.data.GameRepository;
import com.example.gamematch.data.GameRepositoryImpl;
import com.example.gamematch.matching.MatchResult;
import java.util.ArrayList;
import java.util.List;

public class GameViewModel extends AndroidViewModel {

    private final GameRepository repository;

    private final MutableLiveData<FilterParams> currentFilter = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedGameId = new MutableLiveData<>();
    private final MutableLiveData<MatchResult> selectedMatchResult = new MutableLiveData<>();

    public GameViewModel(Application application) {
        super(application);
        this.repository = GameRepositoryImpl.getInstance(application);
    }

    public void searchGames(List<String> moods, String platform) {
        currentFilter.setValue(new FilterParams(new ArrayList<>(moods), platform));
    }

    public LiveData<List<MatchResult>> getRecommendations() {
        return Transformations.switchMap(currentFilter,
                filter -> repository.getRecommendations(filter.moods, filter.platform));
    }

    public List<String> getCurrentMoods() {
        FilterParams params = currentFilter.getValue();
        return params != null ? params.moods : new ArrayList<>();
    }

    public String getCurrentPlatform() {
        FilterParams params = currentFilter.getValue();
        return params != null ? params.platform : "Any platform";
    }

    public void selectMatchResult(MatchResult matchResult) {
        selectedMatchResult.setValue(matchResult);
        if (matchResult != null && matchResult.game != null) {
            selectedGameId.setValue(matchResult.game.id);
        }
    }

    public void selectGameFromFavorites(int gameId) {
        selectedMatchResult.setValue(null);
        selectedGameId.setValue(gameId);
    }

    public void selectGame(int gameId) {
        selectedGameId.setValue(gameId);
    }

    public LiveData<Game> getSelectedGame() {
        return Transformations.switchMap(selectedGameId, repository::getGameById);
    }

    public LiveData<MatchResult> getSelectedMatchResult() {
        return selectedMatchResult;
    }

    public LiveData<List<Game>> getFavorites() {
        return repository.getFavorites();
    }

    public LiveData<Boolean> isFavorite(int gameId) {
        return repository.isFavorite(gameId);
    }

    public void toggleFavorite(int gameId, boolean currentlyFavorite) {
        repository.setFavorite(gameId, !currentlyFavorite);
    }

    private static class FilterParams {
        final List<String> moods;
        final String platform;

        FilterParams(List<String> moods, String platform) {
            this.moods = moods;
            this.platform = platform;
        }
    }
}
