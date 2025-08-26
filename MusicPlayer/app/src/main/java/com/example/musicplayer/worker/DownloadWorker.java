package com.example.musicplayer.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.musicplayer.R;
import com.example.musicplayer.data.db.AppDatabase;
import com.example.musicplayer.data.db.dao.DownloadDao;
import com.example.musicplayer.data.db.dao.TrackDao;
import com.example.musicplayer.data.db.entity.Download;
import com.example.musicplayer.data.db.entity.Track;
import com.example.musicplayer.utils.Constants;
import com.example.musicplayer.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadWorker extends Worker {
    
    private static final String TAG = "DownloadWorker";
    private static final String DOWNLOAD_NOTIFICATION_CHANNEL = "download_channel";
    private static final int DOWNLOAD_NOTIFICATION_ID = 2001;
    
    private DownloadDao downloadDao;
    private TrackDao trackDao;
    private NotificationManager notificationManager;
    
    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        AppDatabase database = AppDatabase.getInstance(context);
        downloadDao = database.downloadDao();
        trackDao = database.trackDao();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }
    
    @NonNull
    @Override
    public Result doWork() {
        String trackId = getInputData().getString(Constants.EXTRA_TRACK_ID);
        if (trackId == null) {
            Log.e(TAG, "Track ID is null");
            return Result.failure();
        }
        
        try {
            // Set foreground info for long-running task
            setForegroundAsync(createForegroundInfo("Starting download..."));
            
            // Get track and download info
            Track track = trackDao.getTrackById(trackId);
            Download download = downloadDao.getDownloadByTrackId(trackId);
            
            if (track == null || download == null) {
                Log.e(TAG, "Track or download not found for ID: " + trackId);
                return Result.failure();
            }
            
            // Check network connectivity
            if (!isNetworkAvailable()) {
                updateDownloadFailed(download.getId(), "No internet connection");
                return Result.retry();
            }
            
            // Check Wi-Fi requirement
            if (isWifiOnlyEnabled() && !isWifiConnected()) {
                updateDownloadFailed(download.getId(), "Wi-Fi connection required");
                return Result.retry();
            }
            
            // Update status to running
            downloadDao.updateDownloadStatus(download.getId(), Download.STATUS_RUNNING);
            
            // Download the track
            boolean success = downloadTrack(track, download);
            
            if (success) {
                // Update track as downloaded
                File trackFile = FileUtils.getTrackFile(getApplicationContext(), trackId);
                trackDao.updateDownloadStatus(trackId, true, trackFile.getAbsolutePath());
                
                // Update download as completed
                downloadDao.updateDownloadCompleted(download.getId(), Download.STATUS_COMPLETED, System.currentTimeMillis());
                
                // Show completion notification
                showCompletionNotification(track.getTitle());
                
                return Result.success();
            } else {
                updateDownloadFailed(download.getId(), "Download failed");
                return Result.failure();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in download worker", e);
            return Result.failure();
        }
    }
    
    private boolean downloadTrack(Track track, Download download) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        
        try {
            // Create download file
            File trackFile = FileUtils.getTrackFile(getApplicationContext(), track.getId());
            File tempFile = new File(trackFile.getAbsolutePath() + ".tmp");
            
            // Check available storage
            URL url = new URL(track.getStreamUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(Constants.CONNECT_TIMEOUT);
            connection.setReadTimeout(Constants.READ_TIMEOUT);
            
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "HTTP error: " + responseCode);
                return false;
            }
            
            long fileSize = connection.getContentLength();
            if (!FileUtils.hasEnoughStorageSpace(getApplicationContext(), fileSize)) {
                Log.e(TAG, "Not enough storage space");
                return false;
            }
            
            inputStream = connection.getInputStream();
            outputStream = new FileOutputStream(tempFile);
            
            byte[] buffer = new byte[8192];
            long totalBytesRead = 0;
            int bytesRead;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                if (isStopped()) {
                    Log.d(TAG, "Download cancelled");
                    return false;
                }
                
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                
                // Update progress
                if (fileSize > 0) {
                    int progress = (int) ((totalBytesRead * 100) / fileSize);
                    downloadDao.updateDownloadProgress(download.getId(), Download.STATUS_RUNNING, progress, totalBytesRead);
                    
                    // Update notification
                    setForegroundAsync(createForegroundInfo("Downloading " + track.getTitle() + " (" + progress + "%)"));
                }
            }
            
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            connection.disconnect();
            
            // Move temp file to final location
            if (tempFile.renameTo(trackFile)) {
                Log.d(TAG, "Download completed: " + track.getTitle());
                return true;
            } else {
                Log.e(TAG, "Failed to rename temp file");
                tempFile.delete();
                return false;
            }
            
        } catch (IOException e) {
            Log.e(TAG, "Download error", e);
            return false;
        } finally {
            try {
                if (outputStream != null) outputStream.close();
                if (inputStream != null) inputStream.close();
                if (connection != null) connection.disconnect();
            } catch (IOException e) {
                Log.e(TAG, "Error closing streams", e);
            }
        }
    }
    
    private void updateDownloadFailed(long downloadId, String reason) {
        downloadDao.updateDownloadFailed(downloadId, Download.STATUS_FAILED, reason, System.currentTimeMillis());
    }
    
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    
    private boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }
    
    private boolean isWifiOnlyEnabled() {
        // Check SharedPreferences for Wi-Fi only setting
        return getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(Constants.PREF_WIFI_ONLY_DOWNLOADS, true);
    }
    
    private ForegroundInfo createForegroundInfo(String message) {
        createNotificationChannel();
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), DOWNLOAD_NOTIFICATION_CHANNEL)
                .setContentTitle("Music Download")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_download)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        
        return new ForegroundInfo(DOWNLOAD_NOTIFICATION_ID, builder.build());
    }
    
    private void showCompletionNotification(String trackTitle) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), DOWNLOAD_NOTIFICATION_CHANNEL)
                .setContentTitle("Download Complete")
                .setContentText(trackTitle + " downloaded successfully")
                .setSmallIcon(R.drawable.ic_download_done)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    DOWNLOAD_NOTIFICATION_CHANNEL,
                    "Downloads",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Music download notifications");
            notificationManager.createNotificationChannel(channel);
        }
    }
}
