# AXTV Android Mobile + TV Starter

Native Android/Kotlin starter for Android phones, tablets, Android TV, and Android TV Box.

Default server:

`http://172.16.50.4/`

## Included

- HTML directory-list parser
- File/folder explorer-style list
- Folder navigation and parent/back
- Refresh
- Video detection
- Media3 / ExoPlayer playback
- HLS / M3U8 support
- Android TV launcher support
- TV remote / D-pad focus support
- Packages / Payment placeholder
- HTTP clear-text network access

## Important server requirement

The server page must expose clickable HTML links such as:

```html
<a href="movies/">Movies</a>
<a href="video.mp4">Video</a>
```

If the server hides its file list and exposes no HTML links or supported API/protocol,
the app cannot discover hidden files automatically.

## Easiest build

1. Install Android Studio.
2. Open this folder as an existing project.
3. Wait for Gradle Sync.
4. Connect an Android phone or Android TV/TV Box.
5. Press Run.

To build an APK:

`Build > Build App Bundle(s) / APK(s) > Build APK(s)`

## Change server

Edit:

`app/src/main/java/com/axtv/app/MainActivity.kt`

Change:

```kotlin
const val BASE_URL = "http://172.16.50.4/"
```

## Payment

Payment is only a placeholder. Production flow should be:

App -> your HTTPS backend -> official payment gateway -> verified callback/webhook -> database -> app checks subscription status.

Do not embed merchant secrets directly in the Android app.

## Network

172.16.50.4 is a private/local address. The phone or TV must be connected to a network that can route to that server.


# Build APK with GitHub Actions (No Android Studio)

This project now includes:

`.github/workflows/build-apk.yml`

## Simple steps

1. Create a new GitHub repository.
2. Upload **all files and folders inside this project**, including the hidden `.github` folder.
3. Commit to the `main` branch.
4. Open the repository's **Actions** tab.
5. Open **Build AXTV APK**.
6. Click **Run workflow** if it did not start automatically.
7. Wait for the workflow to finish.
8. Open the completed workflow run.
9. Under **Artifacts**, download **AXTV-debug-apk**.
10. Extract it to get:

`app-debug.apk`

Install that APK on an Android phone, Android TV, or Android TV Box.

## Important

The phone/TV must be able to open:

`http://172.16.50.4/`

from its own network.

The APK produced by this workflow is a **debug APK**, suitable for testing.
For Play Store/public distribution, create a signed release build with a private keystore.
