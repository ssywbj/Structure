package com.suheng.structure.view.activity;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;

import androidx.annotation.NonNull;

public class MaskCircleDrawable extends LayerDrawable implements Drawable.Callback {
    private static final String TAG = "Wbj";

    /**
     * Creates a new layer drawable with the list of specified layers.
     *
     * @param layers a list of drawables to use as layers in this new drawable,
     *               must be non-null
     */
    public MaskCircleDrawable(@NonNull Drawable[] layers) {
        super(layers);
        Log.d(TAG, "MaskCircleDrawable, layers: " + layers);
    }

    public void startTransition() {
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int level = getLevel();
        Log.d(TAG, "draw, level: " + level);
    }


}
