package com.example.gamematch.ui.favorites;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.gamematch.ui.results.MatchResultAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class FavoritesFragment extends Fragment {

    private GameViewModel viewModel;
    private MatchResultAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        TextView tvCount = view.findViewById(R.id.tv_favorites_count);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_favorites);
        TextView tvEmpty = view.findViewById(R.id.tv_empty_favorites);

        // Réutilise MatchResultAdapter avec la même carte que l'écran Results
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MatchResultAdapter(new MatchResultAdapter.OnResultActionListener() {
            @Override
            public void onGameClick(MatchResult result) {
                Intent intent = new Intent(requireContext(), GameDetailsActivity.class);
                intent.putExtra(GameDetailsActivity.EXTRA_GAME_ID, result.game.id);
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Game game, boolean isCurrentlyFavorite) {
                // Retire le jeu des favoris immédiatement au clic sur le cœur
                viewModel.toggleFavorite(game.id, isCurrentlyFavorite);
            }
        });
        recyclerView.setAdapter(adapter);

        // Observe la liste des jeux favoris en temps réel
        viewModel.getFavorites().observe(getViewLifecycleOwner(), favGames -> {
            int count = favGames != null ? favGames.size() : 0;
            tvCount.setText(count + (count == 1 ? " game worth coming back to" : " games worth coming back to"));

            if (count == 0) {
                recyclerView.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);

                // Convertit chaque Game en MatchResult (score = -1 pour masquer le badge %)
                List<MatchResult> wrappedList = new ArrayList<>();
                Set<Integer> favIds = new HashSet<>();
                for (Game g : favGames) {
                    wrappedList.add(new MatchResult(g, -1, g.description));
                    favIds.add(g.id);
                }
                adapter.setFavoriteIds(favIds);
                adapter.setResults(wrappedList);
            }
        });
    }
}