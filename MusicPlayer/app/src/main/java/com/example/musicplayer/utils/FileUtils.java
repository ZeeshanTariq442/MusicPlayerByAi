package com.example.musicplayer.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {
    
    private static final String TAG = "FileUtils";
    
    /**
     * Get the app's private music download directory
     */
    public static File getMusicDownloadDirectory(Context context) {
        File musicDir = new File(context.getFilesDir(), Constants.DOWNLOAD_DIR);
        if (!musicDir.exists()) {
            musicDir.mkdirs();
        }
        return musicDir;
    }
    
    /**
     * Get the file path for a downloaded track
     */
    public static File getTrackFile(Context context, String trackId) {
        File musicDir = getMusicDownloadDirectory(context);
        return new File(musicDir, trackId + Constants.AUDIO_EXTENSION);
    }
    
    /**
     * Get the file path for a downloaded cover image
     */
    public static File getCoverImageFile(Context context, String trackId) {
        File musicDir = getMusicDownloadDirectory(context);
        return new File(musicDir, trackId + "_cover" + Constants.IMAGE_EXTENSION);
    }
    
    /**
     * Check if a file exists and is readable
     */
    public static boolean fileExists(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        File file = new File(filePath);
        return file.exists() && file.canRead() && file.length() > 0;
    }
    
    /**
     * Check if a track is downloaded locally
     */
    public static boolean isTrackDownloaded(Context context, String trackId) {
        File trackFile = getTrackFile(context, trackId);
        return fileExists(trackFile.getAbsolutePath());
    }
    
    /**
     * Delete a file safely
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        try {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                Log.d(TAG, "File deleted: " + filePath + " - Success: " + deleted);
                return deleted;
            }
            return true; // File doesn't exist, consider it "deleted"
        } catch (Exception e) {
            Log.e(TAG, "Error deleting file: " + filePath, e);
            return false;
        }
    }
    
    /**
     * Delete downloaded track and its cover image
     */
    public static boolean deleteTrackFiles(Context context, String trackId) {
        File trackFile = getTrackFile(context, trackId);
        File coverFile = getCoverImageFile(context, trackId);
        
        boolean trackDeleted = deleteFile(trackFile.getAbsolutePath());
        boolean coverDeleted = deleteFile(coverFile.getAbsolutePath());
        
        return trackDeleted && coverDeleted;
    }
    
    /**
     * Get file size in bytes
     */
    public static long getFileSize(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return 0;
        }
        
        try {
            File file = new File(filePath);
            return file.exists() ? file.length() : 0;
        } catch (Exception e) {
            Log.e(TAG, "Error getting file size: " + filePath, e);
            return 0;
        }
    }
    
    /**
     * Format file size to human readable string
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * Calculate MD5 checksum of a file
     */
    public static String calculateMD5(String filePath) {
        if (!fileExists(filePath)) {
            return null;
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(filePath);
            byte[] buffer = new byte[8192];
            int bytesRead;
            
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            fis.close();
            
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            Log.e(TAG, "Error calculating MD5 for file: " + filePath, e);
            return null;
        }
    }
    
    /**
     * Get available storage space in bytes
     */
    public static long getAvailableStorageSpace(Context context) {
        try {
            File musicDir = getMusicDownloadDirectory(context);
            return musicDir.getUsableSpace();
        } catch (Exception e) {
            Log.e(TAG, "Error getting available storage space", e);
            return 0;
        }
    }
    
    /**
     * Check if there's enough storage space for a download
     */
    public static boolean hasEnoughStorageSpace(Context context, long requiredBytes) {
        long availableSpace = getAvailableStorageSpace(context);
        // Add 10% buffer for safety
        long requiredWithBuffer = (long) (requiredBytes * 1.1);
        return availableSpace > requiredWithBuffer;
    }
    
    /**
     * Clean up old or corrupted files
     */
    public static void cleanupDownloadDirectory(Context context) {
        try {
            File musicDir = getMusicDownloadDirectory(context);
            File[] files = musicDir.listFiles();
            
            if (files != null) {
                for (File file : files) {
                    // Delete files that are 0 bytes (corrupted downloads)
                    if (file.length() == 0) {
                        Log.d(TAG, "Deleting corrupted file: " + file.getName());
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error cleaning up download directory", e);
        }
    }
    
    /**
     * Get total size of all downloaded files
     */
    public static long getTotalDownloadSize(Context context) {
        try {
            File musicDir = getMusicDownloadDirectory(context);
            File[] files = musicDir.listFiles();
            long totalSize = 0;
            
            if (files != null) {
                for (File file : files) {
                    totalSize += file.length();
                }
            }
            return totalSize;
        } catch (Exception e) {
            Log.e(TAG, "Error calculating total download size", e);
            return 0;
        }
    }
}
