package com.example.musicplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.example.musicplayer.service.PlaybackService;
import com.example.musicplayer.utils.Constants;

/**
 * BroadcastReceiver that handles audio becoming noisy events
 * (e.g., headphones unplugged, Bluetooth disconnected)
 */
public class NoisyReceiver extends BroadcastReceiver {
    
    private static final String TAG = "NoisyReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            Log.d(TAG, "Audio becoming noisy - pausing playback");
            
            // Send pause action to PlaybackService
            Intent serviceIntent = new Intent(context, PlaybackService.class);
            serviceIntent.setAction(Constants.ACTION_PAUSE);
            context.startService(serviceIntent);
        }
    }
}
