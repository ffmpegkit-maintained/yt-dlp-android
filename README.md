# yt-dlp-android

An Android library that brings [yt-dlp](https://github.com/yt-dlp/yt-dlp) to Android with a clean Java API — supports [1000+ sites](https://github.com/yt-dlp/yt-dlp/blob/master/supportedsites.md).

[![](https://jitpack.io/v/ffmpegkit-maintained/yt-dlp-android.svg)](https://jitpack.io/#ffmpegkit-maintained/yt-dlp-android)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Build](https://github.com/ffmpegkit-maintained/yt-dlp-android/actions/workflows/publish.yml/badge.svg)](https://github.com/ffmpegkit-maintained/yt-dlp-android/actions/workflows/publish.yml)

## Free vs Pro

| | Free (`yt-dlp-android`) | Pro (`yt-dlp-android-curl`) |
|---|:---:|:---:|
| yt-dlp (1000+ sites) | ✓ | ✓ |
| Java API (`YtDlp`) | ✓ | ✓ |
| Cookie-based auth workaround | ✓ | ✓ |
| **curl-cffi — TLS fingerprint impersonation** | **—** | **✓** |
| Distribution | JitPack (public) | GitHub Packages (token required) |
| Price | Free | $14 / $36 team |

> **What is TLS fingerprint impersonation?**
> Some sites block standard HTTP clients by analyzing the TLS fingerprint of the request. `curl-cffi` reproduces the exact fingerprint of a real browser (Chrome, Firefox…), bypassing this protection without needing cookies or login.
> This feature is **exclusive to the Pro version** (`yt-dlp-android-curl`).
> The free version uses a cookie-based workaround instead — see [Cookie Authentication](https://github.com/ffmpegkit-maintained/yt-dlp-android/wiki/Cookie-Authentication).

---

## Installation (free version)

### Option A — Maven Central (recommended)

```groovy
// build.gradle — no extra repository needed
dependencies {
    implementation 'dev.ffmpegkit-maintained:yt-dlp-android:2.0.2'
}
```

### Option B — JitPack

```groovy
// settings.gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

// build.gradle
dependencies {
    implementation 'dev.ffmpegkit-maintained:yt-dlp-android:2.0.2'
}
```

---

## Usage

### Initialize (once, in Application or Activity)
```java
try {
    YtDlp.init(context);
} catch (YtDlpException e) {
    Log.e("YtDlp", "Init failed", e);
}
```

### Download a video
```java
String output = getExternalFilesDir(null) + "/%(title)s.%(ext)s";

YtDlpRequest request = new YtDlpRequest("https://example.com/video/...")
        .setOutputTemplate(output)
        .addOption("-f", "best[height<=720]");

YtDlp.executeAsync(request, (progress, eta, line) -> {
    Log.d("YtDlp", progress + "% - ETA " + eta + "s");
});
```

### Sites requiring authentication (with cookies)
```java
// Pass a Netscape-format cookies file exported from any browser session
YtDlpRequest request = new YtDlpRequest("https://example.com/video/...")
        .addOption("--cookies", cookiesFilePath)
        .setOutputTemplate(output);

YtDlp.executeAsync(request, callback);
```

### Update yt-dlp

In-app updates are not supported — yt-dlp is bundled inside the AAR via Chaquopy.
To get the latest yt-dlp, update the library version in your `build.gradle`.

> **Full documentation & tutorials → [GitHub Wiki](https://github.com/ffmpegkit-maintained/yt-dlp-android/wiki)**

---

## Pro version — curl-cffi (TLS fingerprint impersonation)

`curl-cffi` is **not included in the free version**. It is exclusively available in `yt-dlp-android-curl`, the paid Pro library.

Drop-in replacement — same API, no extra configuration:

```java
// Replace YtDlp.init() with YtDlpCurl.init() — everything else is identical
YtDlpCurl.init(context);
```

→ Available on [Gumroad](https://ffmpegkit.gumroad.com/l/fsxef) · $14 individual / $36 team (5 devs) · includes Maven access + updates

**Pro integration:**
```groovy
// settings.gradle
maven {
    url 'https://maven.pkg.github.com/ffmpegkit-maintained/yt-dlp-android-curl'
    credentials {
        username = "YOUR_GITHUB_USERNAME"
        password = "YOUR_ACCESS_TOKEN" // provided after purchase
    }
}

// build.gradle
implementation 'dev.ffmpegkit_maintained:yt-dlp-android-curl:VERSION'  // version provided after purchase
```

---

## From the same maintainer

> **Need to process, encode or transcribe the videos you download?**
>
> [**ffmpeg-kit (maintained fork)**](https://github.com/ffmpegkit-maintained/ffmpeg-kit) — the community-maintained continuation of FFmpegKit (archived April 2025). Same API, updated for Android SDK 35 and 16 KB page sizes. Includes an optional **WhisperKit** tier for on-device speech recognition and SRT subtitle generation — no server required.
>
> The natural companion to yt-dlp-android: download with this library, process with ffmpeg-kit.

---

## Requirements

- Android 7.0+ (API 24)
- ABI: arm64-v8a or x86_64
- `INTERNET` permission

## Architecture

yt-dlp-android embeds a full **Python 3.13 runtime** inside the AAR via [Chaquopy](https://chaquo.com/chaquopy/). No Python installation is needed on the device — the interpreter runs in-process inside your app.

```
Your app
  └─ yt-dlp-android (AAR, ~60–80 MB)
       ├─ Chaquopy runtime (CPython 3.13 · arm64-v8a + x86_64)
       ├─ yt-dlp (pure Python, version fixed at library build time)
       └─ certifi (CA certificates)
```

`YtDlp.init(context)` starts CPython in-process via Chaquopy's JNI bridge (`libpython3.13.so`). Every download call dispatches into `ytdlp_runner.py`, which calls `yt_dlp.YoutubeDL(opts).download([url])` — pure Python, no subprocess, no native yt-dlp binary.

## Limitations

| Limitation | Details |
|---|---|
| **No TLS fingerprint impersonation** | Uses Python's default HTTP stack. Sites that block non-browser TLS fingerprints will return errors. Use the [cookie-based workaround](https://github.com/ffmpegkit-maintained/yt-dlp-android/wiki/Cookie-Authentication) or upgrade to the [Pro version](https://github.com/ffmpegkit-maintained/yt-dlp-android/wiki/Pro-Version). |
| **AAR size ~60–80 MB** | Includes CPython 3.13 for two ABI slices (arm64-v8a + x86_64) plus yt-dlp bytecode. Use Android App Bundles (AAB) + ABI splits to reduce per-device download size. |
| **ABI: arm64-v8a + x86_64 only** | 32-bit devices (armeabi-v7a) are not supported. |
| **yt-dlp not updatable in-app** | Bundled at library build time. Update yt-dlp by bumping the library version in `build.gradle`. |

## What changed in v2.0.0

v2.0.0 replaces the `youtubedl-android` dependency with [Chaquopy](https://chaquo.com/chaquopy/) (Python 3.13 embedded). This makes the free and Pro versions share the same architecture and API — upgrading to Pro is a one-line change.

The Java API is compatible: `YtDlp.init()`, `YtDlpRequest`, `YtDlpResponse`, `DownloadProgressCallback` all work the same. The only removed feature is in-app yt-dlp updates (use a new library release instead).

## License

MIT — see [LICENSE](LICENSE)
