package com.example.gamematch.ui.results;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gamematch.R;
import com.example.gamematch.data.Game;
import com.example.gamematch.matching.MatchResult;
import com.example.gamematch.ui.GameViewModel;
import com.example.gamematch.ui.details.GameDetailsActivity;

import java.util.HashSet;
import java.util.Set;

public class ResultsFragment extends Fragment {

    private GameViewModel viewModel;
    private MatchResultAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        LinearLayout btnBack = view.findViewById(R.id.btn_back_mood);
        LinearLayout layoutFilters = view.findViewById(R.id.layout_active_filters);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_results);
        TextView tvEmpty = view.findViewById(R.id.tv_empty_results);

        // Retour à l'écran Mood Selection
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Configuration de la liste verticale
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MatchResultAdapter(new MatchResultAdapter.OnResultActionListener() {
            @Override
            public void onGameClick(MatchResult item) {
                Intent intent = new Intent(requireContext(), GameDetailsActivity.class);
                intent.putExtra(GameDetailsActivity.EXTRA_GAME_ID, item.game.id);
                intent.putExtra(GameDetailsActivity.EXTRA_MATCH_SCORE, item.score);
                intent.putExtra(GameDetailsActivity.EXTRA_MATCH_EXPLANATION, item.explanation);
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Game game, boolean isCurrentlyFavorite) {
                viewModel.toggleFavorite(game.id, isCurrentlyFavorite);
            }
        });
        recyclerView.setAdapter(adapter);

        // Observer la liste des favoris pour colorer les cœurs en temps réel
        viewModel.getFavorites().observe(getViewLifecycleOwner(), favGames -> {
            Set<Integer> ids = new HashSet<>();
            for (Game g : favGames) {
                ids.add(g.id);
            }
            adapter.setFavoriteIds(ids);
        });

        // Observer les recommandations calculées et mettre à jour l'écran
        viewModel.getRecommendations().observe(getViewLifecycleOwner(), results -> {
            int count = results != null ? results.size() : 0;
            renderFilterBar(layoutFilters, count);

            if (count == 0) {
                recyclerView.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
                adapter.setResults(results);
            }
        });
    }

    // Construit la ligne de pastilles en haut ("Relaxed", "Curious", "PC", "3 matches")
    private void renderFilterBar(LinearLayout container, int matchCount) {
        container.removeAllViews();

        // Pastilles vert foncé pour chaque mood sélectionné
        for (String mood : viewModel.getCurrentMoods()) {
            container.addView(createFilterPill(mood, R.color.surface_banner, R.color.lime_accent));
        }

        // Pastille grise pour la plateforme
        String platform = viewModel.getCurrentPlatform().replace("PC / Windows", "PC");
        container.addView(createFilterPill(platform, R.color.surface_card, R.color.text_secondary));

        // Texte compteur à droite ("3 matches")
        TextView tvCount = new TextView(requireContext());
        tvCount.setText(matchCount + (matchCount <= 1 ? " match" : " matches"));
        tvCount.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        tvCount.setTextSize(13f);
        tvCount.setPadding(12, 0, 0, 0);
        container.addView(tvCount);
    }

    private TextView createFilterPill(String text, int bgColorRes, int textColorRes) {
        TextView pill = new TextView(requireContext());
        pill.setText(text);
        pill.setTextColor(ContextCompat.getColor(requireContext(), textColorRes));
        pill.setTextSize(12f);
        pill.setPadding(28, 12, 28, 12);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(ContextCompat.getColor(requireContext(), bgColorRes));
        bg.setCornerRadius(24f);
        pill.setBackground(bg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(16);
        pill.setLayoutParams(params);
        return pill;
    }
}