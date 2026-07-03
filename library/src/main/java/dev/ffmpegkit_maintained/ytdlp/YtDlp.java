package dev.ffmpegkit_maintained.ytdlp;

import android.content.Context;

import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Main entry point for yt-dlp-android.
 *
 * Usage:
 *   YtDlp.init(context);
 *   YtDlpRequest req = new YtDlpRequest("https://...")
 *       .setOutputTemplate("/path/to/output.mp4");
 *   YtDlpResponse resp = YtDlp.execute(req, callback);
 */
public class YtDlp {

    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static boolean initialized = false;

    private YtDlp() {}

    public static synchronized void init(Context context) throws YtDlpException {
        if (initialized) return;
        try {
            YoutubeDL.getInstance().init(context);
            initialized = true;
        } catch (Exception e) {
            throw new YtDlpException("Failed to initialize yt-dlp", e);
        }
    }

    public static YtDlpResponse execute(YtDlpRequest request,
                                        DownloadProgressCallback callback)
            throws YtDlpException {
        ensureInitialized();
        try {
            YoutubeDLRequest inner = buildInnerRequest(request);
            com.yausername.youtubedl_android.YoutubeDLResponse raw =
                    YoutubeDL.getInstance().execute(inner, (progress, eta, line) -> {
                        if (callback != null) callback.onProgressUpdate(progress, eta, line);
                    });
            return new YtDlpResponse(raw.getExitCode(), raw.getOut(), raw.getErr());
        } catch (Exception e) {
            throw new YtDlpException(e.getMessage(), e);
        }
    }

    public static Future<YtDlpResponse> executeAsync(YtDlpRequest request,
                                                     DownloadProgressCallback callback) {
        return executor.submit((Callable<YtDlpResponse>) () -> execute(request, callback));
    }

    public static void updateYtDlp(Context context, YtDlp.UpdateCallback callback) {
        executor.execute(() -> {
            try {
                YoutubeDL.UpdateStatus status =
                        YoutubeDL.getInstance().updateYoutubeDL(context);
                if (callback != null) callback.onComplete(status.toString());
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    private static YoutubeDLRequest buildInnerRequest(YtDlpRequest request) {
        YoutubeDLRequest inner = new YoutubeDLRequest(request.getUrl());
        if (request.getOutputTemplate() != null) {
            inner.addOption("-o", request.getOutputTemplate());
        }
        List<String> opts = request.getOptions();
        int i = 0;
        while (i < opts.size()) {
            String key = opts.get(i);
            if (i + 1 < opts.size() && !opts.get(i + 1).startsWith("-")) {
                inner.addOption(key, opts.get(i + 1));
                i += 2;
            } else {
                inner.addOption(key);
                i++;
            }
        }
        return inner;
    }

    private static void ensureInitialized() throws YtDlpException {
        if (!initialized) {
            throw new YtDlpException("YtDlp not initialized. Call YtDlp.init(context) first.");
        }
    }

    public interface UpdateCallback {
        void onComplete(String status);
        void onError(String error);
    }
}
