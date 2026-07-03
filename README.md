# yt-dlp-android

An Android library that brings [yt-dlp](https://github.com/yt-dlp/yt-dlp) to Android with a clean Java API — supports [1000+ sites](https://github.com/yt-dlp/yt-dlp/blob/master/supportedsites.md).

> **Solving the impersonation problem**
> If you've hit this error on Android:
> ```
> The extractor is attempting impersonation, but no impersonate target is available.
> ```
> The free version provides a cookie-based workaround.
> The **[Pro version](#pro-version-curl-cffi)** bundles `curl-cffi` compiled for Android, enabling full native impersonation — no cookies required.

[![](https://jitpack.io/v/ffmpegkit-maintained/yt-dlp-android.svg)](https://jitpack.io/#ffmpegkit-maintained/yt-dlp-android)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Build](https://github.com/ffmpegkit-maintained/yt-dlp-android/actions/workflows/publish.yml/badge.svg)](https://github.com/ffmpegkit-maintained/yt-dlp-android/actions/workflows/publish.yml)

---

## Installation (free version)

**Step 1** — Add JitPack to your `settings.gradle`:
```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2** — Add the dependency:
```groovy
dependencies {
    implementation 'com.github.ffmpegkit-maintained:yt-dlp-android:1.0.1'
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

### Update yt-dlp binary
```java
YtDlp.updateYtDlp(context, new YtDlp.UpdateCallback() {
    public void onComplete(String status) { Log.d("YtDlp", "Updated: " + status); }
    public void onError(String error)    { Log.e("YtDlp", "Update failed: " + error); }
});
```

> **Full documentation & tutorials → [GitHub Wiki](https://github.com/ffmpegkit-maintained/yt-dlp-android/wiki)**

---

## Pro version (curl-cffi)

The Pro version bundles `curl-cffi` compiled natively for Android (`arm64-v8a`), enabling yt-dlp's full impersonation support for sites that use TLS fingerprinting.

**Drop-in replacement — same API, no extra configuration.**

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
implementation 'com.lucquebec:yt-dlp-android-curl:2.0.0'
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
- `INTERNET` permission

## License

MIT — see [LICENSE](LICENSE)
