package com.example.musicplayer.data.db.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "downloads",
        foreignKeys = @ForeignKey(entity = Track.class,
                                 parentColumns = "id",
                                 childColumns = "trackId",
                                 onDelete = ForeignKey.CASCADE),
        indices = {@Index("trackId")})
public class Download {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    @NonNull
    private String trackId;
    
    private String status; // QUEUED, RUNNING, PAUSED, FAILED, COMPLETED
    private int progress; // 0-100
    private long bytes;
    private String reason; // Error reason if failed
    private long startedAt;
    private long finishedAt;
    private String workId; // WorkManager work ID

    public static final String STATUS_QUEUED = "QUEUED";
    public static final String STATUS_RUNNING = "RUNNING";
    public static final String STATUS_PAUSED = "PAUSED";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_COMPLETED = "COMPLETED";

    public Download() {}

    public Download(@NonNull String trackId, String status) {
        this.trackId = trackId;
        this.status = status;
        this.progress = 0;
        this.bytes = 0;
        this.reason = null;
        this.startedAt = System.currentTimeMillis();
        this.finishedAt = 0;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @NonNull
    public String getTrackId() { return trackId; }
    public void setTrackId(@NonNull String trackId) { this.trackId = trackId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public long getBytes() { return bytes; }
    public void setBytes(long bytes) { this.bytes = bytes; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public long getStartedAt() { return startedAt; }
    public void setStartedAt(long startedAt) { this.startedAt = startedAt; }

    public long getFinishedAt() { return finishedAt; }
    public void setFinishedAt(long finishedAt) { this.finishedAt = finishedAt; }

    public String getWorkId() { return workId; }
    public void setWorkId(String workId) { this.workId = workId; }

    // Helper methods
    public boolean isCompleted() {
        return STATUS_COMPLETED.equals(status);
    }

    public boolean isFailed() {
        return STATUS_FAILED.equals(status);
    }

    public boolean isRunning() {
        return STATUS_RUNNING.equals(status);
    }

    public String getFormattedSize() {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
}
