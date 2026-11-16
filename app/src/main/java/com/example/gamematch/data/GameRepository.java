package com.example.gamematch.data;

import androidx.lifecycle.LiveData;
import com.example.gamematch.matching.MatchResult;
import java.util.List;

// The shared contract between your UI (Student B) and the database/matching engine (Student A).
// LiveData automatically notifies your screens whenever data updates.
public interface GameRepository {
    LiveData<List<MatchResult>> getRecommendations(List<String> selectedMoods, String platform);
    LiveData<Game> getGameById(int gameId);
    LiveData<List<Game>> getFavorites();
    LiveData<Boolean> isFavorite(int gameId);
    void addFavorite(int gameId);
    void removeFavorite(int gameId);
}