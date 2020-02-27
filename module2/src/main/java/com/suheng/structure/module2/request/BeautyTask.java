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
        return "http://ww1.sinaimg.cn/large/0065oQSqly1frepsy47grj30qo0y97en.jpg";
        //return "http://ww1.sinaimg.cn/large/0065oQSqly1frepsi3o15j30k80oidkd.jpg";
        //return "http://ww1.sinaimg.cn/large/0065oQSqly1fri9zqwzkoj30ql0w3jy0.jpg";
        //return "http://ww1.sinaimg.cn/large/0065oQSqly1frja502w5xj30k80od410.jpg";
    }

}
