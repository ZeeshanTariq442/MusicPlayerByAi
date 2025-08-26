package com.example.musicplayer.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.musicplayer.data.db.entity.Recent;
import com.example.musicplayer.data.db.entity.Track;

import java.util.List;

@Dao
public interface RecentDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecent(Recent recent);
    
    @Delete
    void deleteRecent(Recent recent);
    
    @Query("SELECT t.* FROM tracks t " +
           "INNER JOIN recent r ON t.id = r.trackId " +
           "ORDER BY r.playedAt DESC " +
           "LIMIT :limit")
    LiveData<List<Track>> getRecentTracks(int limit);
    
    @Query("SELECT * FROM recent WHERE trackId = :trackId ORDER BY playedAt DESC LIMIT 1")
    Recent getRecentByTrackId(String trackId);
    
    @Query("DELETE FROM recent WHERE trackId = :trackId")
    void deleteRecentByTrackId(String trackId);
    
    @Query("DELETE FROM recent")
    void deleteAllRecent();
    
    @Query("SELECT COUNT(*) FROM recent")
    int getRecentCount();
    
    // Keep only the latest N recent entries to prevent unlimited growth
    @Query("DELETE FROM recent WHERE id NOT IN (SELECT id FROM recent ORDER BY playedAt DESC LIMIT :maxEntries)")
    void limitRecentEntries(int maxEntries);
}
