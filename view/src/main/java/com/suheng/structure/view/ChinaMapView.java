package com.suheng.structure.view;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class ChinaMapView extends View {
    private static final String TAG = ChinaMapView.class.getSimpleName();
    private static final String NAME_SPACE = "http://schemas.android.com/apk/res/android";
    private final List<PathBean> mPathBeans = new ArrayList<>();
    private Paint mPaint;
    private int mListSize;
    /**
     * 地图实际宽高
     */
    private RectF mRectF;

    public ChinaMapView(Context context) {
        super(context);
        this.init();
    }

    public ChinaMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public ChinaMapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        this.getPathList();
    }

    /**
     * 判断是否竖屏
     */
    private int mWidthSize = -1;
    private int mHeightSize = -1;

    /**
     * 平铺缩放比例
     */
    private float mScaleWidth = 0f;
    private float mScaleHeight = 0f;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mRectF.height() == 0 || mRectF.width() == 0) {
            return;
        }

        //测量大小
        mWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        mHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.i(TAG, "widthSize: " + mWidthSize + ", heightSize: " + mHeightSize);

        //拿来到显示比例
        mScaleHeight = mHeightSize / mRectF.height();
        mScaleWidth = mWidthSize / mRectF.width();

        //测量模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //xml文件中宽高wrap_content
        if (widthMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST) {
            //如果是横屏宽保留最大，高需要适配
            if (mWidthSize < mHeightSize) {
                setMeasuredDimension(mWidthSize, (int) (mRectF.height() * mScaleWidth));
            } else {
                setMeasuredDimension(mWidthSize, mHeightSize);
            }
        } else {
            setMeasuredDimension(mWidthSize, mHeightSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mListSize <= 0) {
            return;
        }

        canvas.save();
        if (mWidthSize > mHeightSize) {
            canvas.scale(mScaleWidth, mScaleHeight);
        } else {
            canvas.scale(mScaleWidth, mScaleWidth);
        }
        mPaint.setColor(Color.parseColor("#AAAAAA"));
        canvas.drawRect(mRectF, mPaint);
        for (int i = 0; i < mListSize; i++) {
            PathBean pathBean = mPathBeans.get(i);

            mPaint.setStrokeWidth(Float.parseFloat(pathBean.getStrokeWidth()));
            mPaint.setColor(Color.parseColor(pathBean.getFillColor()));
            canvas.drawPath(pathBean.getPath(), mPaint);

            /*if ("Xinjiang Uygur".equals(pathBean.getName())) {
                pathBean.getPath().computeBounds(rectF, true);
                mPaint.setColor(Color.parseColor("#08E08C"));
                canvas.drawRect(rectF, mPaint);
            }

            if ("Heilongjiang".equals(pathBean.getName())) {
                pathBean.getPath().computeBounds(rectF, true);
                mPaint.setColor(Color.parseColor("#08E08C"));
                canvas.drawRect(rectF, mPaint);
            }*/
        }
        canvas.restore();
    }

    private final RectF rectF = new RectF();

    private void getPathList() {
        XmlResourceParser parser = getResources().getXml(R.xml.chinahigh);
        try {
            PathBean pathBean;
            RectF rectF = new RectF();
            float left = -1, right = -1, top = -1, bottom = -1;

            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();
                    if ("path".equals(tagName)) {
                        pathBean = new PathBean();

                        pathBean.setName(parser.getAttributeValue(NAME_SPACE, "name"));
                        pathBean.setFillColor(parser.getAttributeValue(NAME_SPACE, "fillColor"));
                        pathBean.setStrokeColor(parser.getAttributeValue(NAME_SPACE, "strokeColor"));
                        pathBean.setStrokeWidth(parser.getAttributeValue(NAME_SPACE, "strokeWidth"));
                        Path path = PathParser.createPathFromPathData(parser.getAttributeValue(NAME_SPACE, "pathData"));
                        pathBean.setPath(path);

                        mPathBeans.add(pathBean);

                        rectF.setEmpty();
                        path.computeBounds(rectF, true);
                        Log.i(TAG, "rectF: " + rectF.toShortString());
                        left = ((left == -1) ? rectF.left : Math.min(left, rectF.left));
                        top = ((top == -1) ? rectF.top : Math.min(top, rectF.top));
                        right = ((right == -1) ? rectF.right : Math.max(right, rectF.right));
                        bottom = ((bottom == -1) ? rectF.bottom : Math.max(bottom, rectF.bottom));
                    }
                }

                parser.next();
            }

            mRectF = new RectF(left, top, right, bottom);
            Log.i(TAG, "mRectF: " + mRectF.toShortString());

            mListSize = mPathBeans.size();
            if (mListSize > 0) {
                measure(getMeasuredWidth(), getMeasuredHeight());
                postInvalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class PathBean {
        private String mName;
        private String mFillColor;
        private String mStrokeColor;
        private String mStrokeWidth;
        private String mPathData;
        private Path mPath;

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public String getFillColor() {
            return mFillColor;
        }

        public void setFillColor(String fillColor) {
            mFillColor = fillColor;
        }

        public String getStrokeColor() {
            return mStrokeColor;
        }

        public void setStrokeColor(String strokeColor) {
            mStrokeColor = strokeColor;
        }

        public String getStrokeWidth() {
            return mStrokeWidth;
        }

        public void setStrokeWidth(String strokeWidth) {
            mStrokeWidth = strokeWidth;
        }

        public String getPathData() {
            return mPathData;
        }

        public void setPathData(String pathData) {
            mPathData = pathData;
        }

        public void setPath(Path path) {
            mPath = path;
        }

        public Path getPath() {
            return mPath;
        }
    }
}
