package com.example.musicplayer.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.musicplayer.data.db.entity.Track;

import java.util.List;

@Dao
public interface TrackDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrack(Track track);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTracks(List<Track> tracks);
    
    @Update
    void updateTrack(Track track);
    
    @Delete
    void deleteTrack(Track track);
    
    @Query("SELECT * FROM tracks WHERE id = :trackId")
    Track getTrackById(String trackId);
    
    @Query("SELECT * FROM tracks WHERE id = :trackId")
    LiveData<Track> getTrackByIdLive(String trackId);
    
    @Query("SELECT * FROM tracks ORDER BY createdAt DESC")
    LiveData<List<Track>> getAllTracks();
    
    @Query("SELECT * FROM tracks WHERE featured = 1 ORDER BY createdAt DESC")
    LiveData<List<Track>> getFeaturedTracks();
    
    @Query("SELECT * FROM tracks ORDER BY createdAt DESC LIMIT :limit")
    LiveData<List<Track>> getNewReleases(int limit);
    
    @Query("SELECT * FROM tracks ORDER BY playCount DESC LIMIT :limit")
    LiveData<List<Track>> getTopCharts(int limit);
    
    @Query("SELECT * FROM tracks WHERE isDownloaded = 1 ORDER BY title ASC")
    LiveData<List<Track>> getDownloadedTracks();
    
    @Query("SELECT * FROM tracks WHERE title LIKE :query OR artist LIKE :query OR album LIKE :query OR tags LIKE :query")
    LiveData<List<Track>> searchTracks(String query);
    
    @Query("UPDATE tracks SET playCount = playCount + 1, lastPlayedAt = :timestamp WHERE id = :trackId")
    void incrementPlayCount(String trackId, long timestamp);
    
    @Query("UPDATE tracks SET isDownloaded = :isDownloaded, localPath = :localPath WHERE id = :trackId")
    void updateDownloadStatus(String trackId, boolean isDownloaded, String localPath);
    
    @Query("SELECT COUNT(*) FROM tracks")
    int getTrackCount();
    
    @Query("DELETE FROM tracks")
    void deleteAllTracks();
    
    @Query("SELECT * FROM tracks WHERE lastPlayedAt > 0 ORDER BY lastPlayedAt DESC LIMIT :limit")
    LiveData<List<Track>> getRecentlyPlayed(int limit);
    
    @Query("SELECT * FROM tracks WHERE playCount > 0 ORDER BY playCount DESC LIMIT :limit")
    LiveData<List<Track>> getMostPlayed(int limit);
}
