package com.wiz.watch.facephoto.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.wiz.watch.facephoto.PhotoWatchFace;
import com.wiz.watch.facephoto.R;

public class PhotoWatchFacePreview extends FrameLayout {
    protected boolean mIsEditMode;

    public PhotoWatchFacePreview(Context context) {
        super(context);
        this.init(context);
    }

    public PhotoWatchFacePreview(Context context, boolean isEditMode) {
        super(context);
        mIsEditMode = isEditMode;
        this.init(context);
    }

    public PhotoWatchFacePreview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_photo_face_preview, this);
        ((PhotoWatchFace) findViewById(R.id.photo_watch_face)).setEditMode(mIsEditMode);
    }
}
