package com.suheng.structure.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ListItemLayout extends RelativeLayout {
    private String mTitle, mSubtitle, mDescribeTitle, mDescribeSubtitle;

    private Drawable mLeftDrawable, mRightDrawable;
    private int mLeftDrawableDimen, mRightLayoutType;
    private boolean mIsSubProgressbar, mIsSubSeekbar;

    public ListItemLayout(Context context) {
        super(context);
        this.init();
    }

    public ListItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ListItemLayout);
        int indexCount = typedArray.getIndexCount();
        Log.d("Wbj", "ListItemLayout: " + indexCount);
        for (int at = 0; at < indexCount; at++) {
            final int index = typedArray.getIndex(at);

            if (index == R.styleable.ListItemLayout_lil_title) {
                mTitle = typedArray.getString(index);
                Log.d("Wbj", "ListItemLayout, title: " + mTitle);
            } else if (index == R.styleable.ListItemLayout_lil_subtitle) {
                mSubtitle = typedArray.getString(index);
                Log.i("Wbj", "ListItemLayout, subtitle: " + mSubtitle);
            } else if (index == R.styleable.ListItemLayout_lil_describe_title) {
                mDescribeTitle = typedArray.getString(index);
                Log.v("Wbj", "ListItemLayout, describeTitle: " + mDescribeTitle);
            } else if (index == R.styleable.ListItemLayout_lil_describe_subtitle) {
                mDescribeSubtitle = typedArray.getString(index);
                Log.w("Wbj", "ListItemLayout, describeSubtitle: " + mDescribeSubtitle);
            } else if (index == R.styleable.ListItemLayout_lil_left_image) {
                mLeftDrawable = typedArray.getDrawable(index);
                Log.w("Wbj", "ListItemLayout, leftDrawable: " + mLeftDrawable);
            } else if (index == R.styleable.ListItemLayout_lil_left_image_dimen) {
                mLeftDrawableDimen = typedArray.getInt(index, 0);
                Log.w("Wbj", "ListItemLayout, leftDrawableDimen: " + mLeftDrawableDimen);
            } else if (index == R.styleable.ListItemLayout_lil_right_layout_type) {
                mRightLayoutType = typedArray.getInt(index, 0);
                Log.w("Wbj", "ListItemLayout, rightLayoutType: " + mRightLayoutType);
            } else if (index == R.styleable.ListItemLayout_lil_right_image) {
                mRightDrawable = typedArray.getDrawable(index);
                Log.w("Wbj", "ListItemLayout, rightLayoutType: " + mRightDrawable);
            } else if (index == R.styleable.ListItemLayout_lil_sub_progressbar) {
                mIsSubProgressbar = typedArray.getBoolean(index, false);
                Log.w("Wbj", "ListItemLayout, isSubProgressbar: " + mIsSubProgressbar);
            } else if (index == R.styleable.ListItemLayout_lil_sub_seekbar) {
                mIsSubSeekbar = typedArray.getBoolean(index, false);
                Log.w("Wbj", "ListItemLayout, isSubSeekbar: " + mIsSubSeekbar);
            }
        }
        typedArray.recycle();

        this.init();
    }

    public ListItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        setBackgroundColor(Color.WHITE);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int paddingStart = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, metrics);
        int paddingEnd = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, metrics);
        int paddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, metrics);
        setPadding(paddingStart, paddingTop, paddingEnd, paddingTop);

        inflate(getContext(), R.layout.view_list_item_layout, this);

        ImageView leftImage = null;
        if (mLeftDrawable != null) {
            leftImage = (ImageView) ((ViewStub) findViewById(R.id.lil_stub_left_image)).inflate();
            RelativeLayout.LayoutParams leftImageLayoutParams = (LayoutParams) leftImage.getLayoutParams();
            leftImageLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            leftImageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            int dimension;
            if (mLeftDrawableDimen == 1) {
                dimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 38, metrics);
            } else {
                dimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, metrics);
            }
            leftImageLayoutParams.width = dimension;
            leftImageLayoutParams.height = dimension;
            leftImage.setLayoutParams(leftImageLayoutParams);
            leftImage.setImageDrawable(mLeftDrawable);
        }

        if (!TextUtils.isEmpty(mTitle)) {
            View titleLayout = ((ViewStub) findViewById(R.id.lil_stub_title_layout)).inflate();
            RelativeLayout.LayoutParams titleLayoutLayoutParams = (LayoutParams) titleLayout.getLayoutParams();
            if (leftImage == null) {
                titleLayoutLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            } else {
                titleLayoutLayoutParams.addRule(RelativeLayout.END_OF, leftImage.getId());
            }
            titleLayoutLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            titleLayout.setLayoutParams(titleLayoutLayoutParams);

            TextView textTitle = titleLayout.findViewById(R.id.lil_title);
            textTitle.setText(mTitle);

            TextView subtitle = null;
            if (!TextUtils.isEmpty(mSubtitle)) {
                subtitle = (TextView) ((ViewStub) titleLayout.findViewById(R.id.lil_stub_subtitle)).inflate();
                subtitle.setText(mSubtitle);
            }

            if (mIsSubProgressbar) {
                ProgressBar progressBar = (ProgressBar) ((ViewStub) titleLayout.findViewById(R.id.lil_stub_sub_progressbar)).inflate();
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) progressBar.getLayoutParams();
                if (subtitle == null) {
                    layoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, metrics);
                }
                progressBar.setLayoutParams(layoutParams);
            }

            if (mIsSubSeekbar) {
                SeekBar seekbar = (SeekBar) ((ViewStub) titleLayout.findViewById(R.id.lil_stub_sub_seekbar)).inflate();
                paddingStart = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, metrics);
                seekbar.setPadding(paddingStart, 0, paddingStart, 0);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) seekbar.getLayoutParams();
                if (subtitle == null) {
                    layoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, metrics);
                }
                seekbar.setLayoutParams(layoutParams);
            }
        }

        ImageView rightImage = null;
        View layoutRight = null;
        if (mRightLayoutType == 2) {
            rightImage = (ImageView) ((ViewStub) findViewById(R.id.lil_stub_right_image)).inflate();
            if (mRightDrawable == null) {
                rightImage.setImageResource(R.drawable.vector_delete);
            } else {
                rightImage.setImageDrawable(mRightDrawable);
            }
            layoutRight = rightImage;
        } else if (mRightLayoutType == 3) {
            layoutRight = ((ViewStub) findViewById(R.id.lil_stub_right_radio)).inflate();
        } else if (mRightLayoutType == 4) {
            layoutRight = ((ViewStub) findViewById(R.id.lil_stub_right_switch)).inflate();
        }
        if (layoutRight != null) {
            RelativeLayout.LayoutParams rightImageLayoutParams = (LayoutParams) layoutRight.getLayoutParams();
            rightImageLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            rightImageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            layoutRight.setLayoutParams(rightImageLayoutParams);
        }

        if (rightImage != null && !TextUtils.isEmpty(mDescribeTitle)) {
            View describeLayout = ((ViewStub) findViewById(R.id.lil_stub_describe_layout)).inflate();
            RelativeLayout.LayoutParams describeLayoutLayoutParams = (LayoutParams) describeLayout.getLayoutParams();
            describeLayoutLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            describeLayoutLayoutParams.addRule(RelativeLayout.START_OF, rightImage.getId());
            describeLayout.setLayoutParams(describeLayoutLayoutParams);

            TextView describeTitle = describeLayout.findViewById(R.id.lil_describe_title);
            describeTitle.setText(mDescribeTitle);

            if (!TextUtils.isEmpty(mDescribeSubtitle)) {
                TextView describeSubtitle = (TextView) ((ViewStub) describeLayout.findViewById(R.id.lil_stub_describe_subtitle)).inflate();
                describeSubtitle.setText(mDescribeSubtitle);
            }
        }
    }

    private Path mPath;
    private RectF rectF;

    /*@Override
    public void draw(Canvas canvas) {
        if (mPath == null) {
            mPath = new Path();
            rectF = new RectF();
        }
        mPath.reset();
        rectF.set(0, 0, getWidth(), getHeight());
        float rx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        mPath.addRoundRect(rectF, rx, rx, Path.Direction.CCW);
        canvas.clipPath(mPath);
        super.draw(canvas);
    }*/

}
