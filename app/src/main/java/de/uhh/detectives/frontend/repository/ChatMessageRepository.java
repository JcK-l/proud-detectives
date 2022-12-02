package de.uhh.detectives.frontend.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.uhh.detectives.frontend.model.Message.ChatMessage;

@Dao
public interface ChatMessageRepository {
    @Insert
    void insertAll(final ChatMessage... messages);

    @Insert
    void insert(final ChatMessage message);

    @Query("SELECT * FROM ChatMessage")
    List<ChatMessage> getAll();
}
