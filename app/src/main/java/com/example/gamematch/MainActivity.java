package com.example.gamematch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.gamematch.service.GameReminderService;
import com.example.gamematch.ui.GameViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private GameViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect to the shared ViewModel
        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        // Request runtime notification permission on Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Find the Fragment container and connect it to the bottom navigation bar
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
            NavigationUI.setupWithNavController(bottomNav, navController);

            // Pop back to MoodFragment when tapping "Discover" while on ResultsFragment
            bottomNav.setOnItemReselectedListener(item -> {
                if (item.getItemId() == R.id.moodFragment) {
                    navController.popBackStack(R.id.moodFragment, false);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Cancel pending background reminder when user returns to the app
        stopService(new Intent(this, GameReminderService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Start background reminder service when app moves to the background
        int favCount = viewModel.getFavorites().getValue() != null
                ? viewModel.getFavorites().getValue().size() : 0;
        if (favCount > 0) {
            Intent serviceIntent = new Intent(this, GameReminderService.class);
            serviceIntent.putExtra("fav_count", favCount);
            startService(serviceIntent);
        }
    }
}