package com.example.musicplayer.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.musicplayer.data.db.entity.Playlist;
import com.example.musicplayer.data.db.entity.PlaylistTrack;
import com.example.musicplayer.data.db.entity.Track;

import java.util.List;

@Dao
public interface PlaylistDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlaylist(Playlist playlist);
    
    @Update
    void updatePlaylist(Playlist playlist);
    
    @Delete
    void deletePlaylist(Playlist playlist);
    
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    LiveData<List<Playlist>> getAllPlaylists();
    
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    LiveData<Playlist> getPlaylistById(String playlistId);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlaylistTrack(PlaylistTrack playlistTrack);
    
    @Delete
    void deletePlaylistTrack(PlaylistTrack playlistTrack);
    
    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    void removeTrackFromPlaylist(String playlistId, String trackId);
    
    @Query("SELECT t.* FROM tracks t " +
           "INNER JOIN playlist_tracks pt ON t.id = pt.trackId " +
           "WHERE pt.playlistId = :playlistId " +
           "ORDER BY pt.orderIndex ASC")
    LiveData<List<Track>> getPlaylistTracks(String playlistId);
    
    @Query("SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId")
    int getPlaylistTrackCount(String playlistId);
    
    @Query("SELECT MAX(orderIndex) FROM playlist_tracks WHERE playlistId = :playlistId")
    int getMaxOrderIndex(String playlistId);
    
    @Query("UPDATE playlists SET trackCount = (SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId) WHERE id = :playlistId")
    void updatePlaylistTrackCount(String playlistId);
    
    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId")
    void clearPlaylist(String playlistId);
    
    @Query("SELECT * FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    PlaylistTrack getPlaylistTrack(String playlistId, String trackId);
}
