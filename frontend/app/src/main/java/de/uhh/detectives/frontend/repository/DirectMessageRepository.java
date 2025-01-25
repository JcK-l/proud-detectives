package de.uhh.detectives.frontend.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.uhh.detectives.frontend.model.Message.DirectMessage;

@Dao
public interface DirectMessageRepository {

    @Query("DELETE FROM DirectMessage")
    void deleteAll();

    @Query("UPDATE DirectMessage SET position = position + 1 WHERE position >= 0")
    void prepareForInsertion();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(final DirectMessage directMessage);

    @Query("SELECT * FROM DirectMessage")
    List<DirectMessage> getAll();
}
