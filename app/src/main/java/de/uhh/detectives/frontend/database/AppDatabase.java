package de.uhh.detectives.frontend.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import de.uhh.detectives.frontend.model.Hint;
import de.uhh.detectives.frontend.model.Message.ChatMessage;
import de.uhh.detectives.frontend.model.Message.DirectMessage;
import de.uhh.detectives.frontend.model.Player;
import de.uhh.detectives.frontend.model.Solution;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.repository.ChatMessageRepository;
import de.uhh.detectives.frontend.repository.DirectMessageRepository;
import de.uhh.detectives.frontend.repository.HintRepository;
import de.uhh.detectives.frontend.repository.PlayerRepository;
import de.uhh.detectives.frontend.repository.SolutionRepository;
import de.uhh.detectives.frontend.repository.UserDataRepository;

@Database(entities = {UserData.class, ChatMessage.class, Player.class, Hint.class, Solution.class, DirectMessage.class}
        , version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract UserDataRepository getUserDataRepository();
    public abstract ChatMessageRepository getChatMessageRepository();
    public abstract PlayerRepository getPlayerRepository();
    public abstract HintRepository getHintRepository();
    public abstract SolutionRepository getSolutionRepository();
    public abstract DirectMessageRepository getDirectMessageRepository();


    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "detectives-db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public static AppDatabase getInstance() {
        return INSTANCE;
    }
}