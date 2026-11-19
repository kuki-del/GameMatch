package com.example.gamematch.data.local.db;

import androidx.room.TypeConverter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Converters {

    private static final String DELIMITER = "||";

    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return String.join(DELIMITER, list);
    }

    @TypeConverter
    public static List<String> toList(String value) {
        if (value == null || value.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(value.split("\\|\\|")));
    }
}
