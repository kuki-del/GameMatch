package com.example.gamematch.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.gamematch.data.local.db.Converters;
import java.util.List;

@Entity(tableName = "games")
@TypeConverters(Converters.class)
public class Game {

    @PrimaryKey
    public int id;
    public String title;
    public String description;
    public List<String> platforms;
    public List<String> moods;
    public String coverUrl;
    public String storeUrl;
    public boolean isFavorite;

    // genres ignoré volontairement, pas stocké en base
    public Game(int id, String title, String description, List<String> platforms,
                List<String> moods, List<String> genres, String coverUrl, String storeUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.platforms = platforms;
        this.moods = moods;
        this.coverUrl = coverUrl;
        this.storeUrl = storeUrl;
        this.isFavorite = false;
    }

    public Game() {}
}
