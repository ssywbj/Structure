package com.suheng.structure.module1.request;

import com.suheng.structure.data.net.URLConstants;
import com.suheng.structure.net.request.download.DownloadTask;

import java.io.File;

public class DownloadTaskImpl extends DownloadTask {

    public DownloadTaskImpl(File file) {
        super(file);
    }

    public DownloadTaskImpl(String path) {
        super(path);
    }

    public DownloadTaskImpl(String dirPath, String fileName) {
        super(dirPath, fileName);
    }

    @Override
    protected String getURL() {
        return URLConstants.URL_PICTURE + "/sj15hgvj30sg15hkbw.jpg";
        //return "http://ww1.sinaimg.cn/large/0065oQSqgy1ftt7g8ntdyj30j60op7dq.jpg";
    }

}
