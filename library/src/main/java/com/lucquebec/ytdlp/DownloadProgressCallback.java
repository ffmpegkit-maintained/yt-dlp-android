package com.lucquebec.ytdlp;

public interface DownloadProgressCallback {
    void onProgressUpdate(float progress, long etaInSeconds, String line);
}
