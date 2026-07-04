# yt-dlp-android Pro — Gumroad product description
# Prêt à coller dans le dashboard Gumroad (section "Description")

---

**yt-dlp for Android — Pro (curl-cffi)**

An Android library that runs [yt-dlp](https://github.com/yt-dlp/yt-dlp) on Android via an embedded Python 3.13 runtime (Chaquopy). The Pro version adds `curl-cffi`, which enables TLS fingerprint impersonation — required to download from sites that detect and block non-browser HTTP clients (Instagram, TikTok, some YouTube endpoints).

---

**How it works**

yt-dlp runs inside an embedded CPython 3.13 interpreter (Chaquopy), packaged directly in the AAR. No Python installation is needed on the device. All Python packages — yt-dlp, certifi, and curl-cffi — are pre-bundled.

```
Your app
  └─ yt-dlp-android-curl (AAR)
       ├─ Chaquopy runtime (CPython 3.13, arm64-v8a)
       ├─ yt-dlp (pure Python)
       ├─ certifi (CA certificates)
       └─ curl-cffi 0.15+ (arm64-v8a wheel — TLS impersonation)
```

---

**What's included**

- yt-dlp (latest at AAR build time) + Python 3.13 via Chaquopy
- curl-cffi compiled for arm64-v8a — TLS fingerprint impersonation
- 1000+ supported sites
- Clean Java API: `YtDlpCurl.init()`, `YtDlpRequest`, `YtDlpResponse`, `DownloadProgressCallback`
- Cookie-based auth support (`--cookies`)
- Drop-in replacement for the free version (one-line init change)

---

**Limitations (be aware before purchasing)**

- **ABI: arm64-v8a only** — x86_64 emulators and 32-bit (armeabi-v7a) devices are not supported for curl-cffi. `YtDlpCurl.isCurlAvailable()` returns `false` on unsupported ABIs; the library falls back gracefully to standard HTTP.
- **AAR size: ~80–100 MB** — includes CPython 3.13 + yt-dlp + curl-cffi (one ABI slice). Use Android App Bundles (AAB) to minimize per-device download size.
- **yt-dlp version fixed at build time** — to update yt-dlp, a new AAR version is required. In-app update is not supported.
- **Not a native binary** — yt-dlp runs in Python, not as a compiled ARM64 executable. The "native" in TLS impersonation refers to browser-level TLS fingerprinting, not the runtime architecture.

---

**Free vs Pro**

| Feature | Free | Pro |
|---|:---:|:---:|
| yt-dlp (1000+ sites) | ✓ | ✓ |
| Python 3.13 embedded (Chaquopy) | ✓ | ✓ |
| Java API (YtDlp) | ✓ | ✓ |
| Cookie-based auth | ✓ | ✓ |
| ABI | arm64-v8a + x86_64 | arm64-v8a |
| curl-cffi — TLS fingerprint impersonation | — | ✓ |
| Distribution | Maven Central / JitPack (public) | GitHub Packages (token after purchase) |
| Price | Free / MIT | $24 individual · $62 team (5 devs) — use code **JOKOBEE10** for $10 off |

> 🎉 **Launch promotion** — use code `JOKOBEE10` at checkout for $10 off (Individual: $24→$14 · Team: $62→$52).

---

**After purchase**

You receive a personal GitHub access token by email. Add the private Maven repository to your project:

```groovy
// settings.gradle
maven {
    url 'https://maven.pkg.github.com/ffmpegkit-maintained/yt-dlp-android-curl'
    credentials {
        username = "YOUR_GITHUB_USERNAME"
        password = "YOUR_ACCESS_TOKEN"
    }
}

// build.gradle
implementation 'dev.ffmpegkit_maintained:yt-dlp-android-curl:VERSION'
```

Replace `YtDlp.init()` with `YtDlpCurl.init()` — the rest of the API is identical.

---

**Support:** open a ticket via Gumroad, or email the address provided at purchase.
