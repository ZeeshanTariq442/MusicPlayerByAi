package com.example.musicplayer.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

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

@Database(
    entities = {
        Track.class,
        Playlist.class,
        PlaylistTrack.class,
        Recent.class,
        Favorite.class,
        Download.class
    },
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "music_player_db";
    private static volatile AppDatabase INSTANCE;
    
    public abstract TrackDao trackDao();
    public abstract PlaylistDao playlistDao();
    public abstract RecentDao recentDao();
    public abstract FavoriteDao favoriteDao();
    public abstract DownloadDao downloadDao();
    
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
    
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
