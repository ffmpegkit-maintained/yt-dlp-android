"""
yt-dlp runner for yt-dlp-android free (Chaquopy / Python 3.13).

Called from Java via Chaquopy's Java-Python bridge.
This is the free version — no curl_cffi, no browser impersonation.
For TLS fingerprint impersonation (bypassing bot detection), use yt-dlp-android Pro.
"""
import yt_dlp

# ---------------------------------------------------------------------------
# CLI flag → YoutubeDL option mapping
# ---------------------------------------------------------------------------
_FLAG_MAP = {
    '-f':                    ('format',              True),
    '--format':              ('format',              True),
    '-o':                    ('outtmpl',             True),
    '--output':              ('outtmpl',             True),
    '--cookies':             ('cookiefile',           True),
    '--proxy':               ('proxy',               True),
    '--add-header':          ('_header',              True),
    '--user-agent':          ('_user_agent',          True),
    '--restrict-filenames':  ('restrictfilenames',    False),
    '--newline':             ('newline',              False),
    '--no-playlist':         ('noplaylist',           False),
    '-F':                    ('listformats',          False),
    '-x':                    ('_extract_audio',       False),
    '--extract-audio':       ('_extract_audio',       False),
    '--audio-format':        ('_audio_format',        True),
    '--audio-quality':       ('_audio_quality',       True),
    '--write-sub':           ('writesubtitles',       False),
    '--sub-lang':            ('subtitleslangs',       True),
    '--merge-output-format': ('merge_output_format',  True),
}


def _cli_to_opts(cli_flags):
    """Convert a list of CLI-style flag strings to a YoutubeDL opts dict."""
    opts = {}
    flags = list(cli_flags)
    i = 0
    while i < len(flags):
        flag = flags[i]
        mapping = _FLAG_MAP.get(flag)
        if mapping is None:
            i += 1
            continue
        key, needs_value = mapping
        if not needs_value:
            if key == '_extract_audio':
                opts.setdefault('postprocessors', []).append({
                    'key': 'FFmpegExtractAudio',
                    'preferredcodec': 'mp3',
                    'preferredquality': '192',
                })
                opts['extractaudio'] = True
            else:
                opts[key] = True
            i += 1
        else:
            if i + 1 >= len(flags):
                i += 1
                continue
            val = flags[i + 1]
            if key == '_header':
                name, _, value = val.partition(':')
                opts.setdefault('http_headers', {})[name.strip()] = value.strip()
            elif key == '_user_agent':
                opts.setdefault('http_headers', {})['User-Agent'] = val
            elif key == '_audio_format':
                for pp in opts.get('postprocessors', []):
                    if pp.get('key') == 'FFmpegExtractAudio':
                        pp['preferredcodec'] = val
            elif key == '_audio_quality':
                for pp in opts.get('postprocessors', []):
                    if pp.get('key') == 'FFmpegExtractAudio':
                        pp['preferredquality'] = val
            else:
                opts[key] = val
            i += 2
    return opts


# ---------------------------------------------------------------------------
# Public API (called from Java via Chaquopy)
# ---------------------------------------------------------------------------

def execute(url, output_template, cli_flags, progress_callback=None):
    """
    Execute a yt-dlp download.

    Args:
        url: video URL string
        output_template: yt-dlp output path template or None
        cli_flags: Java String[] / list of CLI flag strings (without URL)
        progress_callback: Java DownloadProgressCallback or None

    Returns:
        int exit code (0 = success)
    """
    return _run(url, output_template, cli_flags, progress_callback=progress_callback)


def execute_debug(url, output_template, cli_flags,
                  log_callback=None,
                  progress_callback=None):
    """
    Like execute but forwards ALL yt-dlp log lines to log_callback.
    Use for debugging integration issues.
    """
    opts = _cli_to_opts(cli_flags or [])
    if output_template:
        opts['outtmpl'] = output_template
    if log_callback is not None:
        class _Logger:
            def debug(self, msg):
                log_callback.onLog('DEBUG' if msg.startswith('[debug]') else 'INFO', msg)
            def warning(self, msg):
                log_callback.onLog('WARNING', msg)
            def error(self, msg):
                log_callback.onLog('ERROR', msg)
        opts['logger'] = _Logger()
    if progress_callback is not None:
        def _hook(d):
            if d['status'] == 'downloading':
                try:
                    pct = float(d.get('_percent_str', '0.0%').strip().replace('%', ''))
                    eta = int(d.get('eta') or 0)
                    line = d.get('_default_template', '')
                    progress_callback.onProgressUpdate(pct, eta, line)
                except Exception:
                    pass
        opts['progress_hooks'] = [_hook]
    with yt_dlp.YoutubeDL(opts) as ydl:
        return ydl.download([url])


# ---------------------------------------------------------------------------
# Internal implementation
# ---------------------------------------------------------------------------

def _run(url, output_template, cli_flags, progress_callback):
    opts = _cli_to_opts(cli_flags or [])

    if output_template:
        opts['outtmpl'] = output_template

    if progress_callback is not None:
        def _hook(d):
            if d['status'] == 'downloading':
                try:
                    pct = float(
                        d.get('_percent_str', '0.0%').strip().replace('%', '')
                    )
                    eta = int(d.get('eta') or 0)
                    line = d.get('_default_template', '')
                    progress_callback.onProgressUpdate(pct, eta, line)
                except Exception:
                    pass
        opts['progress_hooks'] = [_hook]

    with yt_dlp.YoutubeDL(opts) as ydl:
        return ydl.download([url])
