package com.suheng.photo.model;

public class ImageInfo {
    public static ImageInfo getImageInfo() {
        return sImageInfo;
    }

    private static ImageInfo sImageInfo = new ImageInfo();

    public ImageInfo() {
    }

    private String path;
    private String dirName;
    private int width;
    private int height;
    private long size;
    private ImageInfo mImageTitle;

    public ImageInfo getItemFooter() {
        return itemFooter;
    }

    public void setItemFooter(ImageInfo itemFooter) {
        this.itemFooter = itemFooter;
    }

    private ImageInfo itemFooter;

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    private int contentLength;

    public ImageInfo(int contentLength, int itemType) {
        this.contentLength = contentLength;
        this.itemType = itemType;
    }

    public ImageInfo(String title, int itemType) {
        this.title = title;
        this.itemType = itemType;
    }

    public ImageInfo(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    private int itemType;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public long getDateModified() {
        return dateModified;
    }

    public int getOrientation() {
        return orientation;
    }

    private long dateModified;
    private int orientation;

    public ImageInfo(String path, String dirName, int width, int height,
                     long size, long dateModified, int orientation) {
        this.path = path;
        this.dirName = dirName;
        this.width = width;
        this.height = height;
        this.size = size;
        this.dateModified = dateModified;
        this.orientation = orientation;
    }

    public String getPath() {
        return path;
    }

    public String getDirName() {
        return dirName;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getSize() {
        return size;
    }

    public ImageInfo getImageTitle() {
        return mImageTitle;
    }

    public void setImageTitle(ImageInfo imageTitle) {
        mImageTitle = imageTitle;
    }
}
