package com.example.gamematch.ui.results;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gamematch.R;
import com.example.gamematch.data.Game;
import com.example.gamematch.matching.MatchResult;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Gère l'affichage de chaque carte de résultat dans la RecyclerView
public class MatchResultAdapter extends RecyclerView.Adapter<MatchResultAdapter.ResultViewHolder> {

    public interface OnResultActionListener {
        void onGameClick(MatchResult result);
        void onFavoriteClick(Game game, boolean isCurrentlyFavorite);
    }

    private List<MatchResult> results = new ArrayList<>();
    private Set<Integer> favoriteIds = new HashSet<>();
    private final OnResultActionListener listener;

    public MatchResultAdapter(OnResultActionListener listener) {
        this.listener = listener;
    }

    public void setResults(List<MatchResult> newResults) {
        this.results = newResults != null ? newResults : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setFavoriteIds(Set<Integer> ids) {
        this.favoriteIds = ids != null ? ids : new HashSet<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        MatchResult item = results.get(position);
        Game game = item.game;
        Context ctx = holder.itemView.getContext();

        // Remplir le score, le titre, la plateforme et l'explication
        holder.tvScore.setText(item.score + "% match");
        holder.tvTitle.setText(game.title);
        holder.tvExplanation.setText(item.explanation);

        // Affiche le score sur l'écran Results, ou masque le badge sur l'écran Favorites
        if (item.score >= 0) {
            holder.tvScore.setVisibility(View.VISIBLE);
            holder.tvScore.setText(item.score + "% match");
        } else {
            holder.tvScore.setVisibility(View.GONE);
        }

        // Load cover image from URL with Glide, falling back to our dark gradient if offline or null
        Glide.with(ctx)
                .load(game.coverUrl)
                .placeholder(R.drawable.bg_cover_placeholder)
                .error(R.drawable.bg_cover_placeholder)
                .centerCrop()
                .into(holder.imgCover);

        // Afficher les plateformes (raccourcit "PC / Windows" en "PC" comme sur la maquette)
        String platformText = String.join(", ", game.platforms).replace("PC / Windows", "PC");
        holder.tvPlatform.setText(platformText);

        // Mettre à jour l'icône cœur selon que le jeu est en favoris ou non
        boolean isFav = favoriteIds.contains(game.id);
        holder.btnFavorite.setImageResource(isFav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        // Générer dynamiquement les petites pastilles d'ambiances en bas de carte
        holder.layoutMoods.removeAllViews();
        if (game.moods != null) {
            for (String mood : game.moods) {
                TextView pill = new TextView(ctx);
                pill.setText(mood);
                pill.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary));
                pill.setTextSize(11f);
                pill.setPadding(24, 10, 24, 10);

                GradientDrawable bg = new GradientDrawable();
                bg.setColor(ContextCompat.getColor(ctx, R.color.surface_elevated));
                bg.setCornerRadius(20f);
                pill.setBackground(bg);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMarginEnd(16);
                pill.setLayoutParams(params);
                holder.layoutMoods.addView(pill);

            }
        }

        // Pass the full MatchResult (which includes both the Game and its score)
        holder.itemView.setOnClickListener(v -> listener.onGameClick(item));
        // Clic sur le cœur -> ajouter/retirer des favoris
        holder.btnFavorite.setOnClickListener(v -> listener.onFavoriteClick(game, isFav));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvScore, tvTitle, tvPlatform, tvExplanation;
        ImageButton btnFavorite;
        LinearLayout layoutMoods;

        ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.img_game_cover);
            tvScore = itemView.findViewById(R.id.tv_match_score);
            tvTitle = itemView.findViewById(R.id.tv_game_title);
            tvPlatform = itemView.findViewById(R.id.tv_game_platform);
            tvExplanation = itemView.findViewById(R.id.tv_match_explanation);
            btnFavorite = itemView.findViewById(R.id.btn_favorite_card);
            layoutMoods = itemView.findViewById(R.id.layout_game_moods);
        }
    }
}