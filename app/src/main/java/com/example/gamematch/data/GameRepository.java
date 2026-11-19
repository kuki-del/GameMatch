package com.example.gamematch.data;

import androidx.lifecycle.LiveData;
import com.example.gamematch.matching.MatchResult;
import java.util.List;

public interface GameRepository {

    LiveData<List<Game>> getAllGames();
    LiveData<List<Game>> getFavoriteGames();
    LiveData<List<Game>> getFavorites();
    LiveData<Game> getGameById(int gameId);
    LiveData<Boolean> isFavorite(int gameId);
    LiveData<List<MatchResult>> getRecommendations(List<String> selectedMoods, String platform);

    void setFavorite(int gameId, boolean isFavorite);
    void addFavorite(int gameId);
    void removeFavorite(int gameId);
}
