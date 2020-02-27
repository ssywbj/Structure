package com.suheng.structure.net.callback;

import java.io.File;

public interface OnDownloadListener {
    void onDownloading(double percentage, long progress, long total);

    void onDownloadFinish(File file, double takeTime);
}
