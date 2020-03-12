package com.suheng.photo;

import java.util.List;

public abstract class PictureAdapter<T> extends RecyclerAdapter<T> {
    public static final int VIEW_TYPE_CONTENT = 0;
    public static final int VIEW_TYPE_FOOTER = 1;
    public static final int VIEW_TYPE_TITLE = 2;

    public PictureAdapter(List<T> dataList) {
        super(dataList);
    }

}
