package de.uhh.detectives.frontend.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.uhh.detectives.frontend.ui.clues_and_guesses.Cell;

public class Conversion {
    @TypeConverter
    public static List<Cell> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Cell>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(List<Cell> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
