package com.example.musicplayer.data.db.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "recent",
        foreignKeys = @ForeignKey(entity = Track.class,
                                 parentColumns = "id",
                                 childColumns = "trackId",
                                 onDelete = ForeignKey.CASCADE),
        indices = {@Index("trackId")})
public class Recent {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    @NonNull
    private String trackId;
    
    private long playedAt;

    public Recent() {}

    public Recent(@NonNull String trackId, long playedAt) {
        this.trackId = trackId;
        this.playedAt = playedAt;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @NonNull
    public String getTrackId() { return trackId; }
    public void setTrackId(@NonNull String trackId) { this.trackId = trackId; }

    public long getPlayedAt() { return playedAt; }
    public void setPlayedAt(long playedAt) { this.playedAt = playedAt; }
}
