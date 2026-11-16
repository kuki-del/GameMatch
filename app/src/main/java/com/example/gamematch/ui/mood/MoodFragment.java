package com.example.gamematch.ui.mood;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.gamematch.R;
import com.example.gamematch.ShakeDetector;
import com.example.gamematch.ui.GameViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoodFragment extends Fragment {

    private GameViewModel viewModel;
    private final List<String> selectedMoods = new ArrayList<>();
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mood, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register sensor listener only while this screen is active in the foreground
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister sensor listener when leaving/backgrounding the screen to save battery
        if (sensorManager != null) {
            sensorManager.unregisterListener(shakeDetector);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Connect to the shared GameViewModel owned by MainActivity
        viewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        GridLayout moodGrid = view.findViewById(R.id.mood_grid);
        Spinner platformSpinner = view.findViewById(R.id.spinner_platform);
        MaterialButton findGameButton = view.findViewById(R.id.btn_find_game);

        // 1. Setup the Platform Dropdown with white text on dark background
        String[] platforms = {"Any platform", "PC / Windows", "PlayStation", "Xbox", "Nintendo Switch", "Mobile"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_spinner_item, platforms) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(getResources().getColor(R.color.text_primary, null));
                return tv;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        platformSpinner.setAdapter(adapter);
        platformSpinner.setSelection(1); // Defaults to "PC / Windows"

        // 2. Collect all 9 Chips and enforce the 3-mood max rule
        selectedMoods.clear();
        List<Chip> allMoodChips = new ArrayList<>();

        for (int i = 0; i < moodGrid.getChildCount(); i++) {
            View child = moodGrid.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                allMoodChips.add(chip);
                String moodName = chip.getText().toString();

                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        if (selectedMoods.size() >= 3) {
                            chip.setChecked(false);
                            Toast.makeText(requireContext(), "Pick up to 3 moods max", Toast.LENGTH_SHORT).show();
                        } else {
                            selectedMoods.add(moodName);
                        }
                    } else {
                        selectedMoods.remove(moodName);
                    }
                });
            }
        }

        // 3. Handle "Find my game" button click
        findGameButton.setOnClickListener(v -> {
            if (selectedMoods.isEmpty()) {
                Toast.makeText(requireContext(), "Select at least 1 mood first", Toast.LENGTH_SHORT).show();
                return;
            }
            String chosenPlatform = platformSpinner.getSelectedItem().toString();
            viewModel.searchGames(selectedMoods, chosenPlatform);
            Navigation.findNavController(v).navigate(R.id.action_mood_to_results);
        });

        // 4. Initialize Accelerometer sensor for "Shake to Surprise Me"
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        shakeDetector = new ShakeDetector(() -> {
            if (allMoodChips.size() < 2) return;

            for (Chip chip : allMoodChips) {
                chip.setChecked(false);
            }
            List<Chip> shuffled = new ArrayList<>(allMoodChips);
            Collections.shuffle(shuffled);
            shuffled.get(0).setChecked(true);
            shuffled.get(1).setChecked(true);

            Toast.makeText(requireContext(), "Shaken! Picking random moods...", Toast.LENGTH_SHORT).show();
            findGameButton.performClick();
        });
    }
}