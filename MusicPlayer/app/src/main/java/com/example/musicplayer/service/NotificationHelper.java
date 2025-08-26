package com.example.musicplayer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicplayer.R;
import com.example.musicplayer.data.db.entity.Track;
import com.example.musicplayer.ui.activity.NowPlayingActivity;
import com.example.musicplayer.utils.Constants;

public class NotificationHelper {
    
    private final Context context;
    private final NotificationManager notificationManager;
    
    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }
    
    public Notification buildNotification(MediaSessionCompat.Token sessionToken, Track track, boolean isPlaying) {
        // Create intent for when notification is clicked
        Intent intent = new Intent(context, NowPlayingActivity.class);
        intent.putExtra(Constants.EXTRA_TRACK_ID, track.getId());
        PendingIntent contentIntent = PendingIntent.getActivity(
            context, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Create action intents
        PendingIntent previousIntent = createActionIntent(Constants.ACTION_PREVIOUS);
        PendingIntent playPauseIntent = createActionIntent(isPlaying ? Constants.ACTION_PAUSE : Constants.ACTION_PLAY);
        PendingIntent nextIntent = createActionIntent(Constants.ACTION_NEXT);
        PendingIntent stopIntent = createActionIntent(Constants.ACTION_STOP);
        
        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(track.getTitle())
            .setContentText(track.getArtist())
            .setSubText(track.getAlbum())
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(contentIntent)
            .setDeleteIntent(stopIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_skip_previous, "Previous", previousIntent)
            .addAction(
                isPlaying ? R.drawable.ic_pause : R.drawable.ic_play_arrow,
                isPlaying ? "Pause" : "Play",
                playPauseIntent
            )
            .addAction(R.drawable.ic_skip_next, "Next", nextIntent)
            .setStyle(new MediaStyle()
                .setMediaSession(sessionToken)
                .setShowActionsInCompactView(0, 1, 2)
                .setShowCancelButton(true)
                .setCancelButtonIntent(stopIntent)
            );
        
        // Load album art asynchronously
        loadAlbumArt(track, builder);
        
        return builder.build();
    }
    
    private void loadAlbumArt(Track track, NotificationCompat.Builder builder) {
        if (track.getCoverUrl() != null && !track.getCoverUrl().isEmpty()) {
            Glide.with(context)
                .asBitmap()
                .load(track.getCoverUrl())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        builder.setLargeIcon(resource);
                        notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
                    }
                    
                    @Override
                    public void onLoadCleared(Bitmap placeholder) {
                        // Set default album art
                        Bitmap defaultArt = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_album_art);
                        builder.setLargeIcon(defaultArt);
                        notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
                    }
                });
        } else {
            // Set default album art
            Bitmap defaultArt = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_album_art);
            builder.setLargeIcon(defaultArt);
        }
    }
    
    private PendingIntent createActionIntent(String action) {
        Intent intent = new Intent(context, PlaybackService.class);
        intent.setAction(action);
        return PendingIntent.getService(
            context, 
            action.hashCode(), 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Music playback controls");
            channel.setShowBadge(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    public NotificationManager getNotificationManager() {
        return notificationManager;
    }
}
