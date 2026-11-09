package com.example.chatapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert
    void insertMessage(Message message);

    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    List<Message> getAllMessages();

    @Query("SELECT * FROM messages WHERE (sender = :u1 AND receiver = :u2) OR (sender = :u2 AND receiver = :u1) ORDER BY timestamp ASC")
    List<Message> getMessagesBetween(String u1, String u2);
}
