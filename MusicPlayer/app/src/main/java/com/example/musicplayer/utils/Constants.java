package com.example.musicplayer.utils;

public class Constants {
    
    // Notification
    public static final String NOTIFICATION_CHANNEL_ID = "music_playback_channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "Music Playback";
    public static final int NOTIFICATION_ID = 1001;
    
    // Intent Actions
    public static final String ACTION_PLAY = "com.example.musicplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.musicplayer.ACTION_PAUSE";
    public static final String ACTION_NEXT = "com.example.musicplayer.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "com.example.musicplayer.ACTION_PREVIOUS";
    public static final String ACTION_STOP = "com.example.musicplayer.ACTION_STOP";
    
    // Intent Extras
    public static final String EXTRA_TRACK_ID = "track_id";
    public static final String EXTRA_TRACK_LIST = "track_list";
    public static final String EXTRA_CURRENT_POSITION = "current_position";
    public static final String EXTRA_PLAYLIST_ID = "playlist_id";
    
    // SharedPreferences
    public static final String PREFS_NAME = "music_player_prefs";
    public static final String PREF_THEME_MODE = "theme_mode";
    public static final String PREF_WIFI_ONLY_DOWNLOADS = "wifi_only_downloads";
    public static final String PREF_STREAMING_QUALITY = "streaming_quality";
    public static final String PREF_LAST_TRACK_ID = "last_track_id";
    public static final String PREF_LAST_POSITION = "last_position";
    public static final String PREF_SHUFFLE_MODE = "shuffle_mode";
    public static final String PREF_REPEAT_MODE = "repeat_mode";
    
    // Theme modes
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_AUTO = 2;
    
    // Streaming quality
    public static final String QUALITY_AUTO = "auto";
    public static final String QUALITY_LOW = "low";
    public static final String QUALITY_MEDIUM = "medium";
    public static final String QUALITY_HIGH = "high";
    
    // Repeat modes
    public static final int REPEAT_OFF = 0;
    public static final int REPEAT_ONE = 1;
    public static final int REPEAT_ALL = 2;
    
    // Download directory
    public static final String DOWNLOAD_DIR = "music_downloads";
    
    // File extensions
    public static final String AUDIO_EXTENSION = ".mp3";
    public static final String IMAGE_EXTENSION = ".jpg";
    
    // Network timeouts
    public static final int CONNECT_TIMEOUT = 30000; // 30 seconds
    public static final int READ_TIMEOUT = 30000; // 30 seconds
    
    // Database limits
    public static final int MAX_RECENT_ENTRIES = 100;
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    // UI constants
    public static final int MINI_PLAYER_HEIGHT_DP = 64;
    public static final int SEEK_BAR_UPDATE_INTERVAL = 1000; // 1 second
    
    // WorkManager tags
    public static final String WORK_TAG_DOWNLOAD = "download_work";
    public static final String WORK_TAG_CLEANUP = "cleanup_work";
    
    // Firebase collections
    public static final String FIREBASE_TRACKS_COLLECTION = "tracks";
    public static final String FIREBASE_PLAYLISTS_COLLECTION = "playlists";
    
    // Error messages
    public static final String ERROR_NO_INTERNET = "No internet connection";
    public static final String ERROR_TRACK_NOT_FOUND = "Track not found";
    public static final String ERROR_PLAYBACK_FAILED = "Playback failed";
    public static final String ERROR_DOWNLOAD_FAILED = "Download failed";
    public static final String ERROR_WIFI_REQUIRED = "Wi-Fi connection required for downloads";
    
    // Success messages
    public static final String SUCCESS_TRACK_ADDED_TO_PLAYLIST = "Track added to playlist";
    public static final String SUCCESS_TRACK_REMOVED_FROM_PLAYLIST = "Track removed from playlist";
    public static final String SUCCESS_ADDED_TO_FAVORITES = "Added to favorites";
    public static final String SUCCESS_REMOVED_FROM_FAVORITES = "Removed from favorites";
    public static final String SUCCESS_DOWNLOAD_STARTED = "Download started";
    public static final String SUCCESS_DOWNLOAD_COMPLETED = "Download completed";
}
