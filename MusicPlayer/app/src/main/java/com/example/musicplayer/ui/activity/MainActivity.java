package com.example.musicplayer.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.data.db.entity.Track;
import com.example.musicplayer.databinding.ActivityMainBinding;
import com.example.musicplayer.service.PlaybackService;
import com.example.musicplayer.ui.fragment.HomeFragment;
import com.example.musicplayer.ui.fragment.LibraryFragment;
import com.example.musicplayer.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    
    private ActivityMainBinding binding;
    private PlaybackService playbackService;
    private boolean isServiceBound = false;
    
    // Mini player views
    private View miniPlayerContainer;
    private ImageView miniPlayerAlbumArt;
    private TextView miniPlayerTitle;
    private TextView miniPlayerArtist;
    private ImageView miniPlayerPlayPause;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setupBottomNavigation();
        setupMiniPlayer();
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
        
        // Bind to PlaybackService
        bindToPlaybackService();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isServiceBound) {
            unbindService(this);
            isServiceBound = false;
        }
    }
    
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = binding.bottomNavigation;
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_library) {
                selectedFragment = new LibraryFragment();
            } else if (itemId == R.id.nav_downloads) {
                startActivity(new Intent(this, DownloadsActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }
    
    private void setupMiniPlayer() {
        miniPlayerContainer = binding.miniPlayerContainer;
        miniPlayerAlbumArt = binding.miniPlayerAlbumArt;
        miniPlayerTitle = binding.miniPlayerTitle;
        miniPlayerArtist = binding.miniPlayerArtist;
        miniPlayerPlayPause = binding.miniPlayerPlayPause;
        
        // Initially hide mini player
        miniPlayerContainer.setVisibility(View.GONE);
        
        // Set click listeners
        miniPlayerContainer.setOnClickListener(v -> {
            Intent intent = new Intent(this, NowPlayingActivity.class);
            startActivity(intent);
        });
        
        miniPlayerPlayPause.setOnClickListener(v -> {
            if (isServiceBound && playbackService != null) {
                if (playbackService.isPlaying()) {
                    playbackService.pause();
                } else {
                    playbackService.play();
                }
            }
        });
    }
    
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
    
    private void bindToPlaybackService() {
        Intent intent = new Intent(this, PlaybackService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }
    
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        PlaybackService.PlaybackBinder binder = (PlaybackService.PlaybackBinder) service;
        playbackService = binder.getService();
        isServiceBound = true;
        
        // Update mini player with current track
        updateMiniPlayer();
    }
    
    @Override
    public void onServiceDisconnected(ComponentName name) {
        playbackService = null;
        isServiceBound = false;
    }
    
    private void updateMiniPlayer() {
        if (playbackService != null) {
            Track currentTrack = playbackService.getCurrentTrack();
            if (currentTrack != null) {
                showMiniPlayer(currentTrack, playbackService.isPlaying());
            } else {
                hideMiniPlayer();
            }
        }
    }
    
    private void showMiniPlayer(Track track, boolean isPlaying) {
        miniPlayerContainer.setVisibility(View.VISIBLE);
        
        miniPlayerTitle.setText(track.getTitle());
        miniPlayerArtist.setText(track.getArtist());
        
        // Load album art
        Glide.with(this)
            .load(track.getCoverUrl())
            .placeholder(R.drawable.default_album_art)
            .error(R.drawable.default_album_art)
            .into(miniPlayerAlbumArt);
        
        // Update play/pause button
        miniPlayerPlayPause.setImageResource(
            isPlaying ? R.drawable.ic_pause : R.drawable.ic_play_arrow
        );
    }
    
    private void hideMiniPlayer() {
        miniPlayerContainer.setVisibility(View.GONE);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Update mini player when returning to activity
        if (isServiceBound) {
            updateMiniPlayer();
        }
    }
    
    // Public method for fragments to play tracks
    public void playTrack(Track track) {
        if (isServiceBound && playbackService != null) {
            playbackService.playTrack(track);
            showMiniPlayer(track, true);
        } else {
            // Start service and play track
            Intent intent = new Intent(this, PlaybackService.class);
            intent.setAction(Constants.ACTION_PLAY);
            intent.putExtra(Constants.EXTRA_TRACK_ID, track.getId());
            startService(intent);
        }
    }
    
    // Public method to get playback service
    public PlaybackService getPlaybackService() {
        return isServiceBound ? playbackService : null;
    }
}
