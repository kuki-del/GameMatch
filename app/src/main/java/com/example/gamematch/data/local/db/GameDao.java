package com.example.gamematch.data.local.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.gamematch.data.Game;
import java.util.List;

@Dao
public interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Game game);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Game> games);

    @Query("SELECT * FROM games ORDER BY title ASC")
    LiveData<List<Game>> getAllGames();

    @Query("SELECT * FROM games WHERE isFavorite = 1 ORDER BY title ASC")
    LiveData<List<Game>> getFavoriteGames();

    @Query("SELECT * FROM games WHERE id = :gameId LIMIT 1")
    LiveData<Game> getGameById(int gameId);

    @Query("UPDATE games SET isFavorite = :isFavorite WHERE id = :gameId")
    void setFavorite(int gameId, boolean isFavorite);

    @Query("SELECT COUNT(*) FROM games WHERE isFavorite = 1")
    int getFavoriteCount();
}
