package dev.ffmpegkit_maintained.ytdlp;

import java.util.ArrayList;
import java.util.List;

public class YtDlpRequest {

    private final String url;
    private final List<String> options = new ArrayList<>();
    private String outputTemplate;

    public YtDlpRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public YtDlpRequest addOption(String key) {
        options.add(key);
        return this;
    }

    public YtDlpRequest addOption(String key, String value) {
        options.add(key);
        options.add(value);
        return this;
    }

    public YtDlpRequest setOutputTemplate(String template) {
        this.outputTemplate = template;
        return this;
    }

    public String getOutputTemplate() {
        return outputTemplate;
    }

    public List<String> getOptions() {
        return options;
    }
}
