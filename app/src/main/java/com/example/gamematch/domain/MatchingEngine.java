package com.example.gamematch.domain;

import com.example.gamematch.data.Game;
import com.example.gamematch.matching.MatchResult;
import java.util.ArrayList;
import java.util.List;

public class MatchingEngine {

    public static final String PLATFORM_ALL = "Any platform";

    public List<MatchResult> match(List<Game> allGames, List<String> selectedMoods, String platform) {
        List<MatchResult> results = new ArrayList<>();

        if (allGames == null || selectedMoods == null || selectedMoods.isEmpty()) {
            return results;
        }

        for (Game game : allGames) {
            if (!PLATFORM_ALL.equals(platform) && platform != null) {
                if (game.platforms == null || !game.platforms.contains(platform)) {
                    continue;
                }
            }

            List<String> matchedMoods = new ArrayList<>();
            if (game.moods != null) {
                for (String mood : selectedMoods) {
                    if (game.moods.contains(mood)) {
                        matchedMoods.add(mood);
                    }
                }
            }

            if (!matchedMoods.isEmpty()) {
                int score = (int) Math.round((100.0 * matchedMoods.size()) / selectedMoods.size());
                String explanation = buildExplanation(matchedMoods);
                results.add(new MatchResult(game, score, explanation));
            }
        }

        results.sort((a, b) -> Integer.compare(b.score, a.score));
        return results.size() > 5 ? results.subList(0, 5) : results;
    }

    private String buildExplanation(List<String> matchedMoods) {
        if (matchedMoods.size() == 1) {
            return "Matches your " + matchedMoods.get(0) + " mood.";
        }
        String last = matchedMoods.get(matchedMoods.size() - 1);
        List<String> others = matchedMoods.subList(0, matchedMoods.size() - 1);
        return "Matches your " + String.join(", ", others) + " and " + last + " moods.";
    }
}
