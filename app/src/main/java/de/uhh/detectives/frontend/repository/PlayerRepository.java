package de.uhh.detectives.frontend.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.uhh.detectives.frontend.model.Player;

@Dao
public interface PlayerRepository {

    @Query("DELETE FROM Player")
    void deleteAll();

    @Insert
    void insertAll(final List<Player> players);

    @Insert
    void insert(final Player player);

    @Query("SELECT * FROM Player")
    List<Player> getAll();

    @Query("SELECT * FROM Player WHERE id == :userId")
    Player getPlayerWithUserId(Long userId);

    @Query("SELECT * FROM Player LIMIT 1")
    Player findFirst();
}
