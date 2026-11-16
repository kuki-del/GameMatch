package com.example.gamematch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tvTitle = findViewById(R.id.tv_splash_title);
        TextView tvTagline = findViewById(R.id.tv_splash_tagline);

        // 1. Animate "GAME MATCH": fade in + subtle scale up
        tvTitle.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(700)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // 2. Animate "Play how you feel.": slight delay, then fade in + glide up
        tvTagline.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(350)
                .setDuration(650)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // 3. Transition smoothly to MainActivity after 1.8 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish(); // Prevents returning to splash screen on Back press
        }, 1800);
    }
}