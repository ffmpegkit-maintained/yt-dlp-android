package dev.ffmpegkit_maintained.ytdlp;

public interface DownloadProgressCallback {
    void onProgressUpdate(float progress, long etaInSeconds, String line);
}
