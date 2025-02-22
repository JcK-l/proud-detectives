package de.uhh.detectives.frontend.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import de.uhh.detectives.frontend.model.CluesGuessesState;
import de.uhh.detectives.frontend.model.Hint;
import de.uhh.detectives.frontend.model.Map;
import de.uhh.detectives.frontend.model.Message.ChatMessage;
import de.uhh.detectives.frontend.model.Message.DirectMessage;
import de.uhh.detectives.frontend.model.Player;
import de.uhh.detectives.frontend.model.Solution;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.repository.ChatMessageRepository;
import de.uhh.detectives.frontend.repository.CluesGuessesStateRepository;
import de.uhh.detectives.frontend.repository.DirectMessageRepository;
import de.uhh.detectives.frontend.repository.HintRepository;
import de.uhh.detectives.frontend.repository.MapRepository;
import de.uhh.detectives.frontend.repository.PlayerRepository;
import de.uhh.detectives.frontend.repository.SolutionRepository;
import de.uhh.detectives.frontend.repository.UserDataRepository;

@Database(
        entities = {UserData.class, ChatMessage.class, Player.class, Hint.class,
                Solution.class, DirectMessage.class, CluesGuessesState.class, Map.class},
        version = 3,
        exportSchema = false)
@TypeConverters({Conversion.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract UserDataRepository getUserDataRepository();
    public abstract ChatMessageRepository getChatMessageRepository();
    public abstract PlayerRepository getPlayerRepository();
    public abstract HintRepository getHintRepository();
    public abstract SolutionRepository getSolutionRepository();
    public abstract DirectMessageRepository getDirectMessageRepository();
    public abstract CluesGuessesStateRepository getCluesGuessesStateRepository();
    public abstract MapRepository getMapRepository();


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