package com.example.musicplayer.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.musicplayer.data.db.entity.Download;

import java.util.List;

@Dao
public interface DownloadDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDownload(Download download);
    
    @Update
    void updateDownload(Download download);
    
    @Delete
    void deleteDownload(Download download);
    
    @Query("SELECT * FROM downloads ORDER BY startedAt DESC")
    LiveData<List<Download>> getAllDownloads();
    
    @Query("SELECT * FROM downloads WHERE id = :downloadId")
    Download getDownloadById(long downloadId);
    
    @Query("SELECT * FROM downloads WHERE trackId = :trackId")
    Download getDownloadByTrackId(String trackId);
    
    @Query("SELECT * FROM downloads WHERE trackId = :trackId")
    LiveData<Download> getDownloadByTrackIdLive(String trackId);
    
    @Query("SELECT * FROM downloads WHERE status = :status ORDER BY startedAt DESC")
    LiveData<List<Download>> getDownloadsByStatus(String status);
    
    @Query("UPDATE downloads SET status = :status WHERE id = :downloadId")
    void updateDownloadStatus(long downloadId, String status);
    
    @Query("UPDATE downloads SET progress = :progress WHERE id = :downloadId")
    void updateDownloadProgress(long downloadId, int progress);
    
    @Query("UPDATE downloads SET status = :status, progress = :progress, bytes = :bytes WHERE id = :downloadId")
    void updateDownloadProgress(long downloadId, String status, int progress, long bytes);
    
    @Query("UPDATE downloads SET status = :status, reason = :reason, finishedAt = :finishedAt WHERE id = :downloadId")
    void updateDownloadFailed(long downloadId, String status, String reason, long finishedAt);
    
    @Query("UPDATE downloads SET status = :status, progress = 100, finishedAt = :finishedAt WHERE id = :downloadId")
    void updateDownloadCompleted(long downloadId, String status, long finishedAt);
    
    @Query("UPDATE downloads SET workId = :workId WHERE id = :downloadId")
    void updateWorkId(long downloadId, String workId);
    
    @Query("DELETE FROM downloads WHERE trackId = :trackId")
    void deleteDownloadByTrackId(String trackId);
    
    @Query("SELECT COUNT(*) FROM downloads WHERE status = :status")
    int getDownloadCountByStatus(String status);
    
    @Query("SELECT * FROM downloads WHERE status IN ('QUEUED', 'RUNNING') ORDER BY startedAt ASC")
    List<Download> getActiveDownloads();
    
    @Query("DELETE FROM downloads WHERE status = 'COMPLETED'")
    void deleteCompletedDownloads();
    
    @Query("DELETE FROM downloads WHERE status = 'FAILED'")
    void deleteFailedDownloads();
}
