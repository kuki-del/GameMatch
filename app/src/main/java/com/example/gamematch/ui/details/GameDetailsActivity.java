package com.example.gamematch.ui.details;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.gamematch.R;
import com.example.gamematch.data.Game;
import com.example.gamematch.ui.GameViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class GameDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_GAME_ID = "extra_game_id";
    public static final String EXTRA_MATCH_SCORE = "extra_match_score";
    public static final String EXTRA_MATCH_EXPLANATION = "extra_match_explanation";

    private GameViewModel viewModel;
    private boolean isCurrentlyFavorite = false;
    private int currentGameId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        // Read data passed via Intent from ResultsFragment or FavoritesFragment
        currentGameId = getIntent().getIntExtra(EXTRA_GAME_ID, -1);
        int matchScore = getIntent().getIntExtra(EXTRA_MATCH_SCORE, -1);
        String matchExplanation = getIntent().getStringExtra(EXTRA_MATCH_EXPLANATION);

        ImageButton btnBack = findViewById(R.id.btn_details_back);
        ImageButton btnFavorite = findViewById(R.id.btn_details_favorite);
        ImageView imgCover = findViewById(R.id.img_details_cover);
        TextView tvTitle = findViewById(R.id.tv_details_title);
        TextView tvGenres = findViewById(R.id.tv_details_genres);
        LinearLayout layoutTags = findViewById(R.id.layout_details_tags);
        MaterialCardView cardMatchBanner = findViewById(R.id.card_match_banner);
        TextView tvScore = findViewById(R.id.tv_details_score);
        TextView tvExplanation = findViewById(R.id.tv_details_explanation);
        TextView tvDescription = findViewById(R.id.tv_details_description);
        MaterialButton btnLearnMore = findViewById(R.id.btn_learn_more);

        // Close this Activity and return to MainActivity
        btnBack.setOnClickListener(v -> finish());

        // Show match score banner only if launched from Results (score >= 0)
        if (matchScore >= 0) {
            cardMatchBanner.setVisibility(View.VISIBLE);
            tvScore.setText(matchScore + "%");
            tvExplanation.setText(matchExplanation != null ? matchExplanation : "");
        } else {
            cardMatchBanner.setVisibility(View.GONE);
        }

        // Load the selected game details via ViewModel
        if (currentGameId != -1) {
            viewModel.selectGame(currentGameId);
        }

        viewModel.getSelectedGame().observe(this, game -> {
            if (game == null) return;

            tvTitle.setText(game.title);
            tvDescription.setText(game.description);



            Glide.with(this)
                    .load(game.coverUrl)
                    .placeholder(R.drawable.bg_cover_placeholder)
                    .error(R.drawable.bg_cover_placeholder)
                    .centerCrop()
                    .into(imgCover);

            renderTags(layoutTags, game);

            // External store link via implicit Intent
            if (game.storeUrl != null && !game.storeUrl.trim().isEmpty()) {
                btnLearnMore.setVisibility(View.VISIBLE);
                btnLearnMore.setOnClickListener(v -> {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(game.storeUrl));
                        startActivity(browserIntent);
                    } catch (Exception e) {
                        Toast.makeText(this, "Unable to open link", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                btnLearnMore.setVisibility(View.GONE);
            }
        });

        // Observe favorite state
        viewModel.getFavorites().observe(this, favs -> {
            isCurrentlyFavorite = false;
            if (favs != null) {
                for (Game f : favs) {
                    if (f.id == currentGameId) {
                        isCurrentlyFavorite = true;
                        break;
                    }
                }
            }
            btnFavorite.setImageResource(
                    isCurrentlyFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        });

        btnFavorite.setOnClickListener(v -> {
            if (currentGameId != -1) {
                viewModel.toggleFavorite(currentGameId, isCurrentlyFavorite);
                Toast.makeText(this,
                        isCurrentlyFavorite ? "Removed from Favorites" : "Saved to Favorites",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderTags(LinearLayout container, Game game) {
        container.removeAllViews();
        if (game.platforms != null) {
            for (String platform : game.platforms) {
                String label = platform.replace("PC / Windows", "PC");
                container.addView(createPill(label, R.color.surface_card, R.color.text_secondary));
            }
        }
        if (game.moods != null) {
            for (String mood : game.moods) {
                container.addView(createPill(mood, R.color.surface_banner, R.color.lime_accent));
            }
        }
    }

    private TextView createPill(String text, int bgColorRes, int textColorRes) {
        TextView pill = new TextView(this);
        pill.setText(text);
        pill.setTextColor(ContextCompat.getColor(this, textColorRes));
        pill.setTextSize(12f);
        pill.setPadding(28, 12, 28, 12);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(ContextCompat.getColor(this, bgColorRes));
        bg.setCornerRadius(24f);
        pill.setBackground(bg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(16);
        pill.setLayoutParams(params);
        return pill;
    }
}