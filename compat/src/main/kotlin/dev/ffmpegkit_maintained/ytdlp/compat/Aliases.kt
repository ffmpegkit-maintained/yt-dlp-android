package dev.ffmpegkit_maintained.ytdlp.compat

// Migration shim: map yausername/youtubedl-android names to this library's API.
// Add dev.ffmpegkit-maintained:yt-dlp-android-compat to your dependencies,
// then add "import dev.ffmpegkit_maintained.ytdlp.compat.*" — no other code changes needed.
typealias YoutubeDL           = dev.ffmpegkit_maintained.ytdlp.YtDlp
typealias YoutubeDLRequest    = dev.ffmpegkit_maintained.ytdlp.YtDlpRequest
typealias YoutubeDLResponse   = dev.ffmpegkit_maintained.ytdlp.YtDlpResponse
typealias YoutubeDLException  = dev.ffmpegkit_maintained.ytdlp.YtDlpException
typealias YoutubeDLProgressCallback = dev.ffmpegkit_maintained.ytdlp.DownloadProgressCallback
