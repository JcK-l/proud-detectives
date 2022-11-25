package de.uhh.detectives.frontend.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.repository.UserDataRepository;

@Database(entities = {UserData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract UserDataRepository getUserDataRepository();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "detectives-db")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public static AppDatabase getInstance() {
        return INSTANCE;
    }
}