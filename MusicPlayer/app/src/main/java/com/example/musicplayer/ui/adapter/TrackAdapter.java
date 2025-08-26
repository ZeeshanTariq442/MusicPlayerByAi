package com.example.musicplayer.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.data.db.entity.Track;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {
    
    private List<Track> tracks;
    private OnTrackClickListener listener;
    
    public interface OnTrackClickListener {
        void onTrackClick(Track track);
        void onTrackLongClick(Track track);
    }
    
    public TrackAdapter(List<Track> tracks, OnTrackClickListener listener) {
        this.tracks = tracks;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_track, parent, false);
        return new TrackViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.bind(track);
    }
    
    @Override
    public int getItemCount() {
        return tracks.size();
    }
    
    public void updateTracks(List<Track> newTracks) {
        this.tracks = newTracks;
        notifyDataSetChanged();
    }
    
    class TrackViewHolder extends RecyclerView.ViewHolder {
        private ImageView albumArt;
        private TextView title;
        private TextView artist;
        private TextView duration;
        private ImageView downloadIndicator;
        
        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            albumArt = itemView.findViewById(R.id.album_art);
            title = itemView.findViewById(R.id.track_title);
            artist = itemView.findViewById(R.id.track_artist);
            duration = itemView.findViewById(R.id.track_duration);
            downloadIndicator = itemView.findViewById(R.id.download_indicator);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTrackClick(tracks.get(position));
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTrackLongClick(tracks.get(position));
                    return true;
                }
                return false;
            });
        }
        
        public void bind(Track track) {
            title.setText(track.getTitle());
            artist.setText(track.getArtist());
            duration.setText(track.getFormattedDuration());
            
            // Load album art
            Glide.with(itemView.getContext())
                    .load(track.getCoverUrl())
                    .placeholder(R.drawable.default_album_art)
                    .error(R.drawable.default_album_art)
                    .into(albumArt);
            
            // Show download indicator if track is downloaded
            downloadIndicator.setVisibility(track.isDownloaded() ? View.VISIBLE : View.GONE);
        }
    }
}
