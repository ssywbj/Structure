package com.suheng.structure.module1.request;

import com.suheng.structure.data.net.URLConstants;
import com.suheng.structure.net.request.download.DownloadTask;

import java.io.File;

public class DownloadTaskImpl2 extends DownloadTask {

    public DownloadTaskImpl2(File file) {
        super(file);
    }

    public DownloadTaskImpl2(String path) {
        super(path);
    }

    public DownloadTaskImpl2(String dirPath, String fileName) {
        super(dirPath, fileName);
    }

    @Override
    protected String getURL() {
        return URLConstants.URL_FILE + "/ic_launcher.png";
    }

}
