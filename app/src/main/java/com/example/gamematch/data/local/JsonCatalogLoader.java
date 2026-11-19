package com.example.gamematch.data.local;

import android.content.Context;
import com.example.gamematch.data.Game;
import com.example.gamematch.data.local.db.AppDatabase;
import com.example.gamematch.data.local.db.GameDao;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonCatalogLoader {

    public static void seedIfNeeded(Context context) {
        try {
            AppDatabase db = AppDatabase.getInstance(context);
            GameDao dao = db.gameDao();

            InputStream is = context.getAssets().open("games.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray array = new JSONArray(json);
            List<Game> games = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                Game game = new Game();
                game.id          = obj.getInt("id");
                game.title       = obj.getString("title");
                game.description = obj.optString("description", "");
                game.coverUrl    = obj.optString("coverUrl", null);
                game.storeUrl    = obj.optString("storeUrl", null);
                game.isFavorite  = false;
                game.platforms   = jsonArrayToList(obj.optJSONArray("platforms"));
                game.moods       = jsonArrayToList(obj.optJSONArray("moods"));

                games.add(game);
            }

            dao.insertAll(games);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        if (jsonArray == null) return list;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                list.add(jsonArray.getString(i));
            } catch (Exception ignored) {}
        }
        return list;
    }
}
