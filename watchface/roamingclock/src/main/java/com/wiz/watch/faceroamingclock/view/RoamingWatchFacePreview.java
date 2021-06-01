package com.wiz.watch.faceroamingclock.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.wiz.watch.faceroamingclock.R;
import com.wiz.watch.faceroamingclock.RoamingWatchFace;

public class RoamingWatchFacePreview extends FrameLayout {
    protected boolean mIsEditMode;

    public RoamingWatchFacePreview(Context context) {
        super(context);
        this.init(context);
    }

    public RoamingWatchFacePreview(Context context, boolean isEditMode) {
        this(context);
        mIsEditMode = isEditMode;
    }

    public RoamingWatchFacePreview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_roaming_face_preview, this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((RoamingWatchFace) findViewById(R.id.roaming_watch_face)).setEditMode(mIsEditMode);
    }
}
