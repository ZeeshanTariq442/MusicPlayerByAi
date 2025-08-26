package com.example.musicplayer.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.musicplayer.data.db.AppDatabase;
import com.example.musicplayer.data.db.dao.DownloadDao;
import com.example.musicplayer.data.db.dao.FavoriteDao;
import com.example.musicplayer.data.db.dao.PlaylistDao;
import com.example.musicplayer.data.db.dao.RecentDao;
import com.example.musicplayer.data.db.dao.TrackDao;
import com.example.musicplayer.data.db.entity.Download;
import com.example.musicplayer.data.db.entity.Favorite;
import com.example.musicplayer.data.db.entity.Playlist;
import com.example.musicplayer.data.db.entity.PlaylistTrack;
import com.example.musicplayer.data.db.entity.Recent;
import com.example.musicplayer.data.db.entity.Track;
import com.example.musicplayer.data.remote.FirebaseHelper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicRepository {
    
    private static final String TAG = "MusicRepository";
    private static MusicRepository instance;
    
    private final TrackDao trackDao;
    private final PlaylistDao playlistDao;
    private final RecentDao recentDao;
    private final FavoriteDao favoriteDao;
    private final DownloadDao downloadDao;
    private final FirebaseHelper firebaseHelper;
    private final ExecutorService executor;
    
    private MusicRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        trackDao = database.trackDao();
        playlistDao = database.playlistDao();
        recentDao = database.recentDao();
        favoriteDao = database.favoriteDao();
        downloadDao = database.downloadDao();
        firebaseHelper = new FirebaseHelper();
        executor = Executors.newFixedThreadPool(4);
    }
    
    public static synchronized MusicRepository getInstance(Context context) {
        if (instance == null) {
            instance = new MusicRepository(context.getApplicationContext());
        }
        return instance;
    }
    
    // Track operations
    public LiveData<List<Track>> getAllTracks() {
        return trackDao.getAllTracks();
    }
    
    public LiveData<List<Track>> getFeaturedTracks() {
        return trackDao.getFeaturedTracks();
    }
    
    public LiveData<List<Track>> getNewReleases(int limit) {
        return trackDao.getNewReleases(limit);
    }
    
    public LiveData<List<Track>> getTopCharts(int limit) {
        return trackDao.getTopCharts(limit);
    }
    
    public LiveData<List<Track>> getDownloadedTracks() {
        return trackDao.getDownloadedTracks();
    }
    
    public LiveData<List<Track>> searchTracks(String query) {
        return trackDao.searchTracks("%" + query + "%");
    }
    
    public LiveData<Track> getTrackById(String trackId) {
        return trackDao.getTrackByIdLive(trackId);
    }
    
    public void refreshTracksFromFirebase(FirebaseHelper.OnTracksLoadedListener listener) {
        firebaseHelper.getAllTracks(new FirebaseHelper.OnTracksLoadedListener() {
            @Override
            public void onSuccess(List<Track> tracks) {
                executor.execute(() -> {
                    trackDao.insertTracks(tracks);
                    Log.d(TAG, "Inserted " + tracks.size() + " tracks from Firebase");
                });
                listener.onSuccess(tracks);
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error refreshing tracks: " + error);
                listener.onError(error);
            }
        });
    }
    
    public void refreshFeaturedTracks(FirebaseHelper.OnTracksLoadedListener listener) {
        firebaseHelper.getFeaturedTracks(listener);
    }
    
    public void incrementPlayCount(String trackId) {
        executor.execute(() -> {
            trackDao.incrementPlayCount(trackId, System.currentTimeMillis());
            recentDao.insertRecent(new Recent(trackId, System.currentTimeMillis()));
            // Limit recent entries to prevent unlimited growth
            recentDao.limitRecentEntries(100);
        });
    }
    
    // Playlist operations
    public LiveData<List<Playlist>> getAllPlaylists() {
        return playlistDao.getAllPlaylists();
    }
    
    public LiveData<List<Track>> getPlaylistTracks(String playlistId) {
        return playlistDao.getPlaylistTracks(playlistId);
    }
    
    public void createPlaylist(String name, OnOperationCompleteListener listener) {
        executor.execute(() -> {
            try {
                String playlistId = "playlist_" + System.currentTimeMillis();
                Playlist playlist = new Playlist(playlistId, name, System.currentTimeMillis());
                playlistDao.insertPlaylist(playlist);
                listener.onSuccess();
            } catch (Exception e) {
                listener.onError("Failed to create playlist: " + e.getMessage());
            }
        });
    }
    
    public void addTrackToPlaylist(String playlistId, String trackId, OnOperationCompleteListener listener) {
        executor.execute(() -> {
            try {
                int maxOrder = playlistDao.getMaxOrderIndex(playlistId);
                PlaylistTrack playlistTrack = new PlaylistTrack(playlistId, trackId, maxOrder + 1);
                playlistDao.insertPlaylistTrack(playlistTrack);
                playlistDao.updatePlaylistTrackCount(playlistId);
                listener.onSuccess();
            } catch (Exception e) {
                listener.onError("Failed to add track to playlist: " + e.getMessage());
            }
        });
    }
    
    // Favorite operations
    public LiveData<List<Track>> getFavoriteTracks() {
        return favoriteDao.getFavoriteTracks();
    }
    
    public LiveData<Favorite> getFavorite(String trackId) {
        return favoriteDao.getFavoriteLive(trackId);
    }
    
    public void toggleFavorite(String trackId, OnOperationCompleteListener listener) {
        executor.execute(() -> {
            try {
                Favorite existing = favoriteDao.getFavorite(trackId);
                if (existing != null) {
                    favoriteDao.deleteFavorite(existing);
                } else {
                    favoriteDao.insertFavorite(new Favorite(trackId, System.currentTimeMillis()));
                }
                listener.onSuccess();
            } catch (Exception e) {
                listener.onError("Failed to toggle favorite: " + e.getMessage());
            }
        });
    }
    
    // Recent operations
    public LiveData<List<Track>> getRecentTracks(int limit) {
        return recentDao.getRecentTracks(limit);
    }
    
    // Download operations
    public LiveData<List<Download>> getAllDownloads() {
        return downloadDao.getAllDownloads();
    }
    
    public LiveData<Download> getDownloadByTrackId(String trackId) {
        return downloadDao.getDownloadByTrackIdLive(trackId);
    }
    
    public void startDownload(String trackId, OnDownloadStartedListener listener) {
        executor.execute(() -> {
            try {
                // Check if already downloading or downloaded
                Download existing = downloadDao.getDownloadByTrackId(trackId);
                if (existing != null && !existing.isFailed()) {
                    listener.onError("Track is already downloaded or downloading");
                    return;
                }
                
                // Create new download entry
                Download download = new Download(trackId, Download.STATUS_QUEUED);
                long downloadId = downloadDao.insertDownload(download);
                listener.onSuccess(downloadId);
            } catch (Exception e) {
                listener.onError("Failed to start download: " + e.getMessage());
            }
        });
    }
    
    public void updateDownloadStatus(String trackId, boolean isDownloaded, String localPath) {
        executor.execute(() -> {
            trackDao.updateDownloadStatus(trackId, isDownloaded, localPath);
        });
    }
    
    public void deleteDownload(String trackId, OnOperationCompleteListener listener) {
        executor.execute(() -> {
            try {
                downloadDao.deleteDownloadByTrackId(trackId);
                trackDao.updateDownloadStatus(trackId, false, null);
                listener.onSuccess();
            } catch (Exception e) {
                listener.onError("Failed to delete download: " + e.getMessage());
            }
        });
    }
    
    // Interfaces
    public interface OnOperationCompleteListener {
        void onSuccess();
        void onError(String error);
    }
    
    public interface OnDownloadStartedListener {
        void onSuccess(long downloadId);
        void onError(String error);
    }
}
