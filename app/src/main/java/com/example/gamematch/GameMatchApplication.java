package com.example.gamematch;

import android.app.Application;
import com.example.gamematch.data.local.JsonCatalogLoader;
import java.util.concurrent.Executors;

public class GameMatchApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Executors.newSingleThreadExecutor().execute(() ->
                JsonCatalogLoader.seedIfNeeded(getApplicationContext())
        );
    }
}
