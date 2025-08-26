package com.example.musicplayer.data.db.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(tableName = "playlist_tracks",
        primaryKeys = {"playlistId", "trackId"},
        foreignKeys = {
            @ForeignKey(entity = Playlist.class,
                       parentColumns = "id",
                       childColumns = "playlistId",
                       onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = Track.class,
                       parentColumns = "id", 
                       childColumns = "trackId",
                       onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("playlistId"), @Index("trackId")})
public class PlaylistTrack {
    @NonNull
    private String playlistId;
    
    @NonNull
    private String trackId;
    
    private int orderIndex;
    private long addedAt;

    public PlaylistTrack() {}

    public PlaylistTrack(@NonNull String playlistId, @NonNull String trackId, int orderIndex) {
        this.playlistId = playlistId;
        this.trackId = trackId;
        this.orderIndex = orderIndex;
        this.addedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    @NonNull
    public String getPlaylistId() { return playlistId; }
    public void setPlaylistId(@NonNull String playlistId) { this.playlistId = playlistId; }

    @NonNull
    public String getTrackId() { return trackId; }
    public void setTrackId(@NonNull String trackId) { this.trackId = trackId; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public long getAddedAt() { return addedAt; }
    public void setAddedAt(long addedAt) { this.addedAt = addedAt; }
}
