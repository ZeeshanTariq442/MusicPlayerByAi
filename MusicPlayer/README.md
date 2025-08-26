# Android Music Player

A comprehensive, modern Android music player app built in Java with Firebase integration, supporting both online streaming and offline playback.

## Features

### 🎵 Core Playback
- **Online Streaming**: Stream music from Firebase Storage URLs
- **Offline Playback**: Download tracks for offline listening
- **ExoPlayer Integration**: High-quality audio playback with support for various formats
- **Background Playback**: Continues playing when app is minimized
- **Queue Management**: Play, pause, next, previous, shuffle, repeat modes

### 📱 User Interface
- **Material 3 Design**: Modern, clean interface with light/dark theme support
- **Mini Player**: Persistent bottom player with basic controls
- **Now Playing Screen**: Full-screen player with album art and advanced controls
- **Responsive Design**: Optimized for different screen sizes

### 🔥 Firebase Integration
- **Firestore Database**: Store track metadata, playlists, and user data
- **Firebase Storage**: Host audio files and cover art
- **Real-time Sync**: Automatic synchronization of music library

### 💾 Local Storage
- **Room Database**: Local caching and offline data management
- **Download Management**: Background downloads with progress tracking
- **Smart Caching**: Efficient storage management

### 🎧 Advanced Features
- **Playlists**: Create and manage custom playlists
- **Favorites**: Mark tracks as favorites
- **Recently Played**: Track listening history
- **Search**: Find tracks by title, artist, album, or tags
- **Download Queue**: Manage multiple downloads with retry functionality

### 🔧 System Integration
- **Media Session**: Lock screen and notification controls
- **Audio Focus**: Proper audio focus handling
- **Headset Controls**: Support for wired and Bluetooth headset controls
- **Notification**: Rich media notifications with playback controls

## Architecture

### Clean Architecture Pattern
```
├── data/
│   ├── db/           # Room database (entities, DAOs)
│   ├── remote/       # Firebase integration
│   └── repository/   # Data layer coordination
├── service/          # Background services
├── ui/              # User interface (activities, fragments, adapters)
├── worker/          # Background tasks (downloads)
├── receiver/        # Broadcast receivers
└── utils/           # Utility classes and constants
```

### Key Components

#### Database Layer (Room)
- **Track**: Music track metadata
- **Playlist**: User-created playlists
- **Download**: Download status and progress
- **Favorite**: User favorites
- **Recent**: Recently played tracks

#### Service Layer
- **PlaybackService**: Foreground service for music playback
- **NotificationHelper**: Media-style notifications
- **DownloadWorker**: Background download management

#### UI Layer
- **MainActivity**: Main hub with bottom navigation
- **SplashActivity**: App launch screen
- **NowPlayingActivity**: Full-screen player
- **DownloadsActivity**: Download management
- **SettingsActivity**: App preferences

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API 23+ (target API 34)
- Firebase project with Firestore and Storage enabled

### Firebase Setup

1. **Create Firebase Project**
   ```bash
   # Go to https://console.firebase.google.com/
   # Create a new project
   # Enable Firestore Database and Firebase Storage
   ```

2. **Download Configuration**
   - Download `google-services.json` from Firebase Console
   - Place it in `app/` directory

3. **Configure Firestore Security Rules**
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /tracks/{id} { 
         allow read: if true; 
         allow write: if request.auth != null && request.auth.token.admin == true; 
       }
       match /playlists/{id} { 
         allow read: if true; 
         allow write: if request.auth != null && request.auth.token.admin == true; 
       }
     }
   }
   ```

4. **Configure Storage Security Rules**
   ```javascript
   rules_version = '2';
   service firebase.storage {
     match /b/{bucket}/o {
       match /audio/{allPaths=**} { allow read: if true; allow write: if false; }
       match /images/{allPaths=**} { allow read: if true; allow write: if false; }
     }
   }
   ```

### Sample Data Structure

#### Firestore Collection: `tracks`
```json
{
  "title": "Lab Pe Aati Hai",
  "artist": "Allama Iqbal",
  "album": "Classics",
  "durationMs": 213000,
  "streamUrl": "https://firebasestorage.../song.mp3?token=...",
  "coverUrl": "https://firebasestorage.../cover.jpg",
  "tags": ["urdu", "nasheed", "classic"],
  "featured": true,
  "createdAt": 1719950000000
}
```

### Build and Run

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd MusicPlayer
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the MusicPlayer directory

3. **Sync Project**
   - Let Gradle sync the project
   - Resolve any dependency issues

4. **Add Firebase Configuration**
   - Place `google-services.json` in `app/` directory
   - Ensure Firebase is properly configured

5. **Build and Install**
   ```bash
   ./gradlew assembleDebug
   # Or use Android Studio's build button
   ```

## Dependencies

### Core Dependencies
- **Firebase BOM**: `32.7.0`
- **ExoPlayer**: `2.19.1`
- **Room**: `2.6.1`
- **WorkManager**: `2.9.0`
- **Glide**: `4.16.0`
- **Material Components**: `1.11.0`

### Key Features by Dependency
- **ExoPlayer**: Audio streaming and playback
- **Room**: Local database and caching
- **WorkManager**: Background downloads
- **Glide**: Image loading and caching
- **Firebase**: Cloud storage and database

## Permissions

### Required Permissions
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
```

## Usage

### Basic Usage
1. **Launch App**: Open the app to see the splash screen
2. **Browse Music**: Navigate through Home, Library, Downloads, and Settings
3. **Play Music**: Tap any track to start playback
4. **Download**: Long-press tracks to download for offline listening
5. **Create Playlists**: Organize your music into custom playlists

### Advanced Features
- **Offline Mode**: Downloaded tracks play automatically when offline
- **Background Playback**: Music continues when app is minimized
- **Lock Screen Controls**: Control playback from lock screen
- **Notification Controls**: Use notification panel for quick controls

## Testing

### Manual Testing Checklist
- [ ] Cold start under 2 seconds
- [ ] Background playback survives app swipe-away
- [ ] Lock screen controls work on Android 10-14
- [ ] Offline mode: downloaded tracks play in airplane mode
- [ ] Download queue resumes after reboot
- [ ] No crashes during stability testing

### Error Handling
- **No Internet**: Shows offline library only
- **Download Failures**: Retry mechanism with exponential backoff
- **Playback Errors**: Graceful fallback and user notification
- **Storage Full**: Prevents downloads and notifies user

## Contributing

### Code Style
- Follow Android development best practices
- Use Java 8 features where appropriate
- Maintain clean architecture separation
- Add proper error handling and logging

### Pull Request Process
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- **Material Design**: Google's design system
- **ExoPlayer**: Google's media player library
- **Firebase**: Google's mobile development platform
- **Glide**: Image loading library by Bumptech

## Support

For support, please open an issue on GitHub or contact the development team.

---

**Note**: This is a demo application. For production use, implement proper authentication, content licensing, and additional security measures.
