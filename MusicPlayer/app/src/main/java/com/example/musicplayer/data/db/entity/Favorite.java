package com.example.musicplayer.data.db.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "favorites",
        foreignKeys = @ForeignKey(entity = Track.class,
                                 parentColumns = "id",
                                 childColumns = "trackId",
                                 onDelete = ForeignKey.CASCADE),
        indices = {@Index("trackId")})
public class Favorite {
    @PrimaryKey
    @NonNull
    private String trackId;
    
    private long addedAt;

    public Favorite() {}

    public Favorite(@NonNull String trackId, long addedAt) {
        this.trackId = trackId;
        this.addedAt = addedAt;
    }

    // Getters and Setters
    @NonNull
    public String getTrackId() { return trackId; }
    public void setTrackId(@NonNull String trackId) { this.trackId = trackId; }

    public long getAddedAt() { return addedAt; }
    public void setAddedAt(long addedAt) { this.addedAt = addedAt; }
}
