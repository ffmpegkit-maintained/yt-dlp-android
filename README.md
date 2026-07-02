# yt-dlp-android

An Android library that brings [yt-dlp](https://github.com/yt-dlp/yt-dlp) to Android with a clean Java API — download videos from YouTube, Instagram, TikTok, X/Twitter, Facebook and [1000+ sites](https://github.com/yt-dlp/yt-dlp/blob/master/supportedsites.md).

> **Solving the impersonation problem**
> If you've hit this error on Android:
> ```
> The extractor is attempting impersonation, but no impersonate target is available.
> ```
> The free version of this library provides a cookie-based workaround for Instagram.
> The **[Pro version](#pro-version-curl-cffi)** bundles `curl-cffi` compiled for Android, enabling full native impersonation — no cookies required.

[![](https://jitpack.io/v/LucQuebec/yt-dlp-android.svg)](https://jitpack.io/#LucQuebec/yt-dlp-android)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## Installation (free version)

**Step 1** — Add JitPack to your `settings.gradle`:
```groovy
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2** — Add the dependency:
```groovy
dependencies {
    implementation 'com.github.LucQuebec:yt-dlp-android:1.0.0'
}
```

---

## Usage

### Initialize (once, in Application or Activity)
```java
YtDlp.init(context);
```

### Download a video
```java
String output = getExternalFilesDir(null) + "/%(title)s.%(ext)s";

YtDlpRequest request = new YtDlpRequest("https://www.youtube.com/watch?v=...")
        .setOutputTemplate(output)
        .addOption("-f", "best[height<=720]");

YtDlp.executeAsync(request, (progress, eta, line) -> {
    Log.d("YtDlp", progress + "% - ETA " + eta + "s");
});
```

### Instagram (with cookies)
```java
// Build a cookies file from CookieManager (user must be logged in via WebView)
File cookiesFile = InstagramCookieHelper.buildCookieFile(context);

YtDlpRequest request = new YtDlpRequest("https://www.instagram.com/reel/...")
        .addOption("--cookies", cookiesFile.getAbsolutePath())
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

---

## Pro version (curl-cffi)

The Pro version bundles `curl-cffi` compiled natively for Android (`arm64-v8a`, `armeabi-v7a`, `x86_64`), enabling yt-dlp's full impersonation support — required by Instagram, TikTok, and other platforms that use TLS fingerprinting.

**No cookies needed. Drop-in replacement — same API.**

→ Available on [Gumroad](https://gumroad.com) · $9 one-time · includes Maven access + updates for 1 year

---

## Requirements

- Android 7.0+ (API 24)
- `INTERNET` permission

## License

MIT — see [LICENSE](LICENSE)
