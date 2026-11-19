package com.example.gamematch.data;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.example.gamematch.data.local.db.AppDatabase;
import com.example.gamematch.data.local.db.GameDao;
import com.example.gamematch.domain.MatchingEngine;
import com.example.gamematch.matching.MatchResult;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameRepositoryImpl implements GameRepository {

    private static volatile GameRepositoryImpl INSTANCE;

    private final GameDao gameDao;
    private final MatchingEngine matchingEngine;
    private final ExecutorService executor;

    private GameRepositoryImpl(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.gameDao = db.gameDao();
        this.matchingEngine = new MatchingEngine();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public static GameRepositoryImpl getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (GameRepositoryImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GameRepositoryImpl(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public LiveData<List<Game>> getAllGames() {
        return gameDao.getAllGames();
    }

    @Override
    public LiveData<List<Game>> getFavoriteGames() {
        return gameDao.getFavoriteGames();
    }

    @Override
    public LiveData<List<Game>> getFavorites() {
        return getFavoriteGames();
    }

    @Override
    public LiveData<Game> getGameById(int gameId) {
        return gameDao.getGameById(gameId);
    }

    @Override
    public void setFavorite(int gameId, boolean isFavorite) {
        executor.execute(() -> gameDao.setFavorite(gameId, isFavorite));
    }

    @Override
    public void addFavorite(int gameId) {
        setFavorite(gameId, true);
    }

    @Override
    public void removeFavorite(int gameId) {
        setFavorite(gameId, false);
    }

    @Override
    public LiveData<Boolean> isFavorite(int gameId) {
        return Transformations.map(gameDao.getGameById(gameId),
                game -> game != null && game.isFavorite);
    }

    @Override
    public LiveData<List<MatchResult>> getRecommendations(List<String> selectedMoods, String platform) {
        return Transformations.map(gameDao.getAllGames(), allGames ->
                matchingEngine.match(allGames, selectedMoods, platform)
        );
    }

    // synchrone, appelé sur un thread background (ex: GameReminderService)
    public int getFavoriteCountSync() {
        return gameDao.getFavoriteCount();
    }
}
