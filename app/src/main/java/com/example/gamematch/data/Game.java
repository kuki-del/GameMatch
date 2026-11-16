package com.example.gamematch.data;

import java.util.List;

// Represents one video game in our catalogue.
// Student A will add Room database annotations (@Entity, @PrimaryKey) to this class later.
public class Game {
    public int id;                  // Unique ID for each game
    public String title;            // Game title
    public String description;      // Full summary shown on the Details screen
    public List<String> platforms;  // e.g., "PC", "PlayStation", "Xbox", "Nintendo Switch", "Mobile"
    public List<String> moods;      // e.g., "relaxing", "mysterious", "atmospheric"
    public String coverUrl;         // Optional cover image link
    public String storeUrl;         // Optional external store/info link

    public Game(int id, String title, String description, List<String> platforms,
                List<String> moods, List<String> genres, String coverUrl, String storeUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.platforms = platforms;
        this.moods = moods;
        this.coverUrl = coverUrl;
        this.storeUrl = storeUrl;
    }
}