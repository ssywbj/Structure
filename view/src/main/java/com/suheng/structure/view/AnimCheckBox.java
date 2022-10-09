package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class AnimCheckBox extends CheckBox {
    public static final String TAG = AnimCheckBox.class.getSimpleName();

    public AnimCheckBox(Context context) {
        super(context);
        this.init();
    }

    public AnimCheckBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public AnimCheckBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        //setButtonDrawable(ContextCompat.getDrawable(getContext(), R.drawable.checkbox_style_selector));

        StateListDrawable stateListDrawable = new StateListDrawable();
        //Drawable checkedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.checkbox_checked);
        Drawable unenabledDrawable = ContextCompat.getDrawable(getContext(), R.drawable.checkbox_unenabled);
        //Drawable normalDrawable = ContextCompat.getDrawable(getContext(), R.drawable.checkbox_unchecked);
        //Drawable normalDrawable = ContextCompat.getDrawable(getContext(), R.drawable.earth);

        CheckedDrawable checkedDrawable = new CheckedDrawable(Color.BLUE);
        checkedDrawable.setChecked(true);
        checkedDrawable.setNormalBitmap(CheckedDrawable.drawable2Bitmap(ContextCompat.getDrawable(getContext(), R.drawable.checkbox_unchecked)));
        checkedDrawable.setCheckedBitmap(CheckedDrawable.drawable2Bitmap(ContextCompat.getDrawable(getContext(), R.drawable.checkbox_checked_bg)));
        CheckedDrawable normalDrawable = new CheckedDrawable(Color.BLUE);
        normalDrawable.setNormalBitmap(CheckedDrawable.drawable2Bitmap(ContextCompat.getDrawable(getContext(), R.drawable.checkbox_unchecked)));
        normalDrawable.setCheckedBitmap(CheckedDrawable.drawable2Bitmap(ContextCompat.getDrawable(getContext(), R.drawable.checkbox_checked_bg)));
        Log.d(TAG, "init, checkedDrawable: " + checkedDrawable + ", unenabledDrawable: " + unenabledDrawable + ", normalDrawable: " + normalDrawable);

        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, checkedDrawable);
        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, unenabledDrawable);
        stateListDrawable.addState(new int[]{}, normalDrawable);
        setButtonDrawable(stateListDrawable);

        //setEnabled(false);
        //setChecked(true);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        Log.d(TAG, "setChecked, checked: " + checked);
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        super.invalidateDrawable(drawable);
        if (drawable instanceof StateListDrawable) {
            //Log.d(TAG, "invalidateDrawable, drawable: " + drawable);
            StateListDrawable stateListDrawable = (StateListDrawable) drawable;
            int[] state = stateListDrawable.getState();
            StringBuilder sb = new StringBuilder("state: ");
            for (int i : state) {
                sb.append(i).append(", ");
            }
            final int length = sb.length();
            sb.delete(length - 2, length);
            Log.d(TAG, "invalidateDrawable, " + sb + ", isChecked: " + isChecked());
        }
    }

}
