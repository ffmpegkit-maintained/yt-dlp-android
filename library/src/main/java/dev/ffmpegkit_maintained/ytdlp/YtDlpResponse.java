package dev.ffmpegkit_maintained.ytdlp;

public class YtDlpResponse {

    private final int exitCode;
    private final String output;
    private final String errorOutput;

    public YtDlpResponse(int exitCode, String output, String errorOutput) {
        this.exitCode = exitCode;
        this.output = output;
        this.errorOutput = errorOutput;
    }

    public int getExitCode() { return exitCode; }
    public String getOutput() { return output; }
    public String getErrorOutput() { return errorOutput; }
    public boolean isSuccess() { return exitCode == 0; }
}
