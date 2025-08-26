package com.example.musicplayer.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicplayer.R;
import com.example.musicplayer.data.db.entity.Track;
import com.example.musicplayer.data.repository.MusicRepository;
import com.example.musicplayer.databinding.FragmentLibraryBinding;
import com.example.musicplayer.ui.activity.MainActivity;
import com.example.musicplayer.ui.adapter.TrackAdapter;
import com.example.musicplayer.utils.Constants;

import java.util.ArrayList;

public class LibraryFragment extends Fragment implements TrackAdapter.OnTrackClickListener {
    
    private FragmentLibraryBinding binding;
    private MusicRepository repository;
    private TrackAdapter downloadedAdapter;
    private TrackAdapter favoritesAdapter;
    private TrackAdapter recentAdapter;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = MusicRepository.getInstance(requireContext());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupRecyclerViews();
        loadData();
    }
    
    private void setupRecyclerViews() {
        // Downloaded tracks
        downloadedAdapter = new TrackAdapter(new ArrayList<>(), this);
        binding.recyclerDownloaded.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerDownloaded.setAdapter(downloadedAdapter);
        
        // Favorite tracks
        favoritesAdapter = new TrackAdapter(new ArrayList<>(), this);
        binding.recyclerFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerFavorites.setAdapter(favoritesAdapter);
        
        // Recent tracks
        recentAdapter = new TrackAdapter(new ArrayList<>(), this);
        binding.recyclerRecent.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerRecent.setAdapter(recentAdapter);
    }
    
    private void loadData() {
        // Load downloaded tracks
        repository.getDownloadedTracks().observe(getViewLifecycleOwner(), tracks -> {
            if (tracks != null && !tracks.isEmpty()) {
                downloadedAdapter.updateTracks(tracks);
                binding.sectionDownloaded.setVisibility(View.VISIBLE);
                binding.emptyDownloaded.setVisibility(View.GONE);
            } else {
                binding.sectionDownloaded.setVisibility(View.GONE);
                binding.emptyDownloaded.setVisibility(View.VISIBLE);
            }
        });
        
        // Load favorite tracks
        repository.getFavoriteTracks().observe(getViewLifecycleOwner(), tracks -> {
            if (tracks != null && !tracks.isEmpty()) {
                favoritesAdapter.updateTracks(tracks);
                binding.sectionFavorites.setVisibility(View.VISIBLE);
                binding.emptyFavorites.setVisibility(View.GONE);
            } else {
                binding.sectionFavorites.setVisibility(View.GONE);
                binding.emptyFavorites.setVisibility(View.VISIBLE);
            }
        });
        
        // Load recent tracks
        repository.getRecentTracks(20).observe(getViewLifecycleOwner(), tracks -> {
            if (tracks != null && !tracks.isEmpty()) {
                recentAdapter.updateTracks(tracks);
                binding.sectionRecent.setVisibility(View.VISIBLE);
                binding.emptyRecent.setVisibility(View.GONE);
            } else {
                binding.sectionRecent.setVisibility(View.GONE);
                binding.emptyRecent.setVisibility(View.VISIBLE);
            }
        });
    }
    
    @Override
    public void onTrackClick(Track track) {
        // Play the track
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).playTrack(track);
        }
    }
    
    @Override
    public void onTrackLongClick(Track track) {
        // Show options menu
        showTrackOptions(track);
    }
    
    private void showTrackOptions(Track track) {
        // Toggle favorite status
        repository.toggleFavorite(track.getId(), new MusicRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Favorite updated", Toast.LENGTH_SHORT).show();
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to update favorite", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
