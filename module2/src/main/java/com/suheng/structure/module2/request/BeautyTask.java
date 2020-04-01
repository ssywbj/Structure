package com.suheng.structure.module2.request;

import com.suheng.structure.net.request.download.DownloadTask;

import java.io.File;

public class BeautyTask extends DownloadTask {

    public BeautyTask(File file) {
        super(file);
    }

    public BeautyTask(String path) {
        super(path);
    }

    public BeautyTask(String dirPath, String fileName) {
        super(dirPath, fileName);
    }

    @Override
    protected String getURL() {
        //return URLConstants.URL_PICTURE + "/0065oQSqly1fsfq2pwt72j30qo0yg78u.jpg";
        return "http://ww1.sinaimg.cn/large/0065oQSqgy1ftt7g8ntdyj30j60op7dq.jpg";
    }

}
