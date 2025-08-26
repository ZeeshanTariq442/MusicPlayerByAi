package com.example.musicplayer.data.remote;

import android.util.Log;

import com.example.musicplayer.data.db.entity.Track;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {
    
    private static final String TAG = "FirebaseHelper";
    private static final String COLLECTION_TRACKS = "tracks";
    private static final String COLLECTION_PLAYLISTS = "playlists";
    
    private final FirebaseFirestore db;
    
    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
    }
    
    public interface OnTracksLoadedListener {
        void onSuccess(List<Track> tracks);
        void onError(String error);
    }
    
    public interface OnTrackLoadedListener {
        void onSuccess(Track track);
        void onError(String error);
    }
    
    public void getAllTracks(OnTracksLoadedListener listener) {
        db.collection(COLLECTION_TRACKS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Track> tracks = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Track track = documentToTrack(document);
                    if (track != null) {
                        tracks.add(track);
                    }
                }
                listener.onSuccess(tracks);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting tracks", e);
                listener.onError("Failed to load tracks: " + e.getMessage());
            });
    }
    
    public void getFeaturedTracks(OnTracksLoadedListener listener) {
        db.collection(COLLECTION_TRACKS)
            .whereEqualTo("featured", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Track> tracks = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Track track = documentToTrack(document);
                    if (track != null) {
                        tracks.add(track);
                    }
                }
                listener.onSuccess(tracks);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting featured tracks", e);
                listener.onError("Failed to load featured tracks: " + e.getMessage());
            });
    }
    
    public void getNewReleases(int limit, OnTracksLoadedListener listener) {
        db.collection(COLLECTION_TRACKS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Track> tracks = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Track track = documentToTrack(document);
                    if (track != null) {
                        tracks.add(track);
                    }
                }
                listener.onSuccess(tracks);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting new releases", e);
                listener.onError("Failed to load new releases: " + e.getMessage());
            });
    }
    
    public void searchTracks(String query, OnTracksLoadedListener listener) {
        // Note: Firestore doesn't support full-text search natively
        // This is a basic implementation - for production, consider using Algolia or similar
        db.collection(COLLECTION_TRACKS)
            .orderBy("title")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Track> tracks = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Track track = documentToTrack(document);
                    if (track != null) {
                        tracks.add(track);
                    }
                }
                listener.onSuccess(tracks);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error searching tracks", e);
                listener.onError("Failed to search tracks: " + e.getMessage());
            });
    }
    
    private Track documentToTrack(QueryDocumentSnapshot document) {
        try {
            String id = document.getId();
            String title = document.getString("title");
            String artist = document.getString("artist");
            String album = document.getString("album");
            Long durationMs = document.getLong("durationMs");
            String streamUrl = document.getString("streamUrl");
            String coverUrl = document.getString("coverUrl");
            List<String> tagsList = (List<String>) document.get("tags");
            String tags = tagsList != null ? String.join(",", tagsList) : "";
            Boolean featured = document.getBoolean("featured");
            Long createdAt = document.getLong("createdAt");
            
            return new Track(
                id,
                title != null ? title : "",
                artist != null ? artist : "",
                album != null ? album : "",
                durationMs != null ? durationMs : 0,
                streamUrl != null ? streamUrl : "",
                coverUrl != null ? coverUrl : "",
                tags,
                featured != null ? featured : false,
                createdAt != null ? createdAt : System.currentTimeMillis()
            );
        } catch (Exception e) {
            Log.e(TAG, "Error parsing track document", e);
            return null;
        }
    }
}
