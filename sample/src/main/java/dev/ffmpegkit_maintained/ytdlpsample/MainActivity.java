package dev.ffmpegkit_maintained.ytdlpsample;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import dev.ffmpegkit_maintained.ytdlp.DownloadProgressCallback;
import dev.ffmpegkit_maintained.ytdlp.YtDlp;
import dev.ffmpegkit_maintained.ytdlp.YtDlpException;
import dev.ffmpegkit_maintained.ytdlp.YtDlpRequest;
import dev.ffmpegkit_maintained.ytdlp.YtDlpResponse;

public class MainActivity extends Activity {

    private static final String TAG = "YtDlpSample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            YtDlp.init(this);
        } catch (YtDlpException e) {
            Log.e(TAG, "Init failed", e);
            return;
        }

        String outputPath = getExternalFilesDir(null) + "/%(title)s.%(ext)s";

        YtDlpRequest request = new YtDlpRequest("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                .setOutputTemplate(outputPath)
                .addOption("-f", "best[height<=720]");

        YtDlp.executeAsync(request, new DownloadProgressCallback() {
            @Override
            public void onProgressUpdate(float progress, long etaInSeconds, String line) {
                Log.d(TAG, String.format("%.1f%% - ETA %ds - %s", progress, etaInSeconds, line));
            }
        });
    }
}
