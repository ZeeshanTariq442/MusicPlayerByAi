package com.example.musicplayer.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.musicplayer.data.db.entity.Favorite;
import com.example.musicplayer.data.db.entity.Track;

import java.util.List;

@Dao
public interface FavoriteDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavorite(Favorite favorite);
    
    @Delete
    void deleteFavorite(Favorite favorite);
    
    @Query("DELETE FROM favorites WHERE trackId = :trackId")
    void removeFavorite(String trackId);
    
    @Query("SELECT * FROM favorites WHERE trackId = :trackId")
    Favorite getFavorite(String trackId);
    
    @Query("SELECT * FROM favorites WHERE trackId = :trackId")
    LiveData<Favorite> getFavoriteLive(String trackId);
    
    @Query("SELECT t.* FROM tracks t " +
           "INNER JOIN favorites f ON t.id = f.trackId " +
           "ORDER BY f.addedAt DESC")
    LiveData<List<Track>> getFavoriteTracks();
    
    @Query("SELECT COUNT(*) FROM favorites")
    int getFavoriteCount();
    
    @Query("SELECT COUNT(*) FROM favorites WHERE trackId = :trackId")
    int isFavorite(String trackId);
    
    @Query("DELETE FROM favorites")
    void deleteAllFavorites();
}
