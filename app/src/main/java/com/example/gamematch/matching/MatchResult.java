package com.example.gamematch.matching;

import com.example.gamematch.data.Game;

// Bundles a matched Game with its calculated percentage score and explanation text.
public class MatchResult {
    public final Game game;          // The matched game
    public final int score;          // Match percentage (0 to 100)
    public final String explanation; // Short reason (e.g., "Matches your relaxing and atmospheric preferences.")

    public MatchResult(Game game, int score, String explanation) {
        this.game = game;
        this.score = score;
        this.explanation = explanation;
    }
}