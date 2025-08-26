package com.example.musicplayer.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.data.db.entity.Track;
import com.example.musicplayer.data.repository.MusicRepository;
import com.example.musicplayer.databinding.FragmentHomeBinding;
import com.example.musicplayer.ui.activity.MainActivity;
import com.example.musicplayer.ui.adapter.TrackAdapter;
import com.example.musicplayer.utils.Constants;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements TrackAdapter.OnTrackClickListener {
    
    private FragmentHomeBinding binding;
    private MusicRepository repository;
    private TrackAdapter featuredAdapter;
    private TrackAdapter newReleasesAdapter;
    private TrackAdapter topChartsAdapter;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = MusicRepository.getInstance(requireContext());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupRecyclerViews();
        setupSwipeRefresh();
        loadData();
    }
    
    private void setupRecyclerViews() {
        // Featured tracks
        featuredAdapter = new TrackAdapter(new ArrayList<>(), this);
        binding.recyclerFeatured.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerFeatured.setAdapter(featuredAdapter);
        
        // New releases
        newReleasesAdapter = new TrackAdapter(new ArrayList<>(), this);
        binding.recyclerNewReleases.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerNewReleases.setAdapter(newReleasesAdapter);
        
        // Top charts
        topChartsAdapter = new TrackAdapter(new ArrayList<>(), this);
        binding.recyclerTopCharts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerTopCharts.setAdapter(topChartsAdapter);
    }
    
    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            refreshData();
        });
    }
    
    private void loadData() {
        showLoading(true);
        
        // Load featured tracks
        repository.getFeaturedTracks().observe(getViewLifecycleOwner(), tracks -> {
            if (tracks != null && !tracks.isEmpty()) {
                featuredAdapter.updateTracks(tracks);
                binding.sectionFeatured.setVisibility(View.VISIBLE);
            } else {
                binding.sectionFeatured.setVisibility(View.GONE);
            }
        });
        
        // Load new releases
        repository.getNewReleases(10).observe(getViewLifecycleOwner(), tracks -> {
            if (tracks != null && !tracks.isEmpty()) {
                newReleasesAdapter.updateTracks(tracks);
                binding.sectionNewReleases.setVisibility(View.VISIBLE);
            } else {
                binding.sectionNewReleases.setVisibility(View.GONE);
            }
            showLoading(false);
        });
        
        // Load top charts
        repository.getTopCharts(10).observe(getViewLifecycleOwner(), tracks -> {
            if (tracks != null && !tracks.isEmpty()) {
                topChartsAdapter.updateTracks(tracks);
                binding.sectionTopCharts.setVisibility(View.VISIBLE);
            } else {
                binding.sectionTopCharts.setVisibility(View.GONE);
            }
        });
    }
    
    private void refreshData() {
        // Refresh data from Firebase
        repository.refreshTracksFromFirebase(new MusicRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.swipeRefresh.setRefreshing(false);
                        Snackbar.make(binding.getRoot(), "Music library updated", Snackbar.LENGTH_SHORT).show();
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.swipeRefresh.setRefreshing(false);
                        Snackbar.make(binding.getRoot(), "Failed to update: " + error, Snackbar.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
    
    private void showLoading(boolean show) {
        if (binding != null) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            binding.scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
        // Show options menu (download, add to playlist, etc.)
        showTrackOptions(track);
    }
    
    private void showTrackOptions(Track track) {
        // For now, just show a simple download option
        if (track.isDownloaded()) {
            Toast.makeText(getContext(), "Track already downloaded", Toast.LENGTH_SHORT).show();
        } else {
            // Start download
            repository.startDownload(track.getId(), new MusicRepository.OnDownloadStartedListener() {
                @Override
                public void onSuccess(long downloadId) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), Constants.SUCCESS_DOWNLOAD_STARTED, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
                
                @Override
                public void onError(String error) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Download failed: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
