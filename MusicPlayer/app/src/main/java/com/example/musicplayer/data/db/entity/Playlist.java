package com.example.musicplayer.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "playlists")
public class Playlist {
    @PrimaryKey
    @NonNull
    private String id;
    
    private String name;
    private long createdAt;
    private int trackCount;

    public Playlist() {}

    public Playlist(@NonNull String id, String name, long createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.trackCount = 0;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public int getTrackCount() { return trackCount; }
    public void setTrackCount(int trackCount) { this.trackCount = trackCount; }
}
