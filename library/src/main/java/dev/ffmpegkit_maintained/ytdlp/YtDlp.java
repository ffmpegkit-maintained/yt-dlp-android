package dev.ffmpegkit_maintained.ytdlp;

import android.content.Context;

import com.chaquo.python.PyException;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class YtDlp {

    static final ExecutorService executor = Executors.newCachedThreadPool();
    private static volatile boolean initialized = false;

    private YtDlp() {}

    public static synchronized void init(Context context) throws YtDlpException {
        if (initialized) return;
        try {
            if (!Python.isStarted()) {
                Python.start(new AndroidPlatform(context));
            }
            initialized = true;
        } catch (Exception e) {
            throw new YtDlpException("Failed to initialize Python runtime", e);
        }
    }

    public static YtDlpResponse execute(YtDlpRequest request,
                                        DownloadProgressCallback callback)
            throws YtDlpException {
        ensureInitialized();
        try {
            PyObject runner = Python.getInstance().getModule("ytdlp_runner");
            int exitCode = runner
                    .callAttr("execute",
                            request.getUrl(),
                            request.getOutputTemplate(),
                            request.getOptions().toArray(new String[0]),
                            callback)
                    .toJava(Integer.class);
            return new YtDlpResponse(exitCode, "", "");
        } catch (PyException e) {
            throw new YtDlpException(e.getMessage(), e);
        } catch (Exception e) {
            throw new YtDlpException(e.getMessage(), e);
        }
    }

    public static Future<YtDlpResponse> executeAsync(YtDlpRequest request,
                                                     DownloadProgressCallback callback) {
        return executor.submit((Callable<YtDlpResponse>) () -> execute(request, callback));
    }

    public static Future<YtDlpResponse> executeDebug(YtDlpRequest request,
                                                     LogCallback logCallback,
                                                     DownloadProgressCallback progressCallback) {
        return executor.submit((Callable<YtDlpResponse>) () -> {
            ensureInitialized();
            try {
                PyObject runner = Python.getInstance().getModule("ytdlp_runner");
                int exitCode = runner
                        .callAttr("execute_debug",
                                request.getUrl(),
                                request.getOutputTemplate(),
                                request.getOptions().toArray(new String[0]),
                                logCallback,
                                progressCallback)
                        .toJava(Integer.class);
                return new YtDlpResponse(exitCode, "", "");
            } catch (PyException e) {
                throw new YtDlpException(e.getMessage(), e);
            }
        });
    }

    public static void updateYtDlp(Context context, UpdateCallback callback) {
        executor.execute(() -> {
            if (callback != null) {
                callback.onError("In-app yt-dlp update not supported. " +
                        "Update the yt-dlp-android dependency to get the latest yt-dlp.");
            }
        });
    }

    private static void ensureInitialized() throws YtDlpException {
        if (!initialized) {
            throw new YtDlpException(
                    "YtDlp not initialized. Call YtDlp.init(context) first.");
        }
    }

    public interface UpdateCallback {
        void onComplete(String status);
        void onError(String error);
    }
}
