package com.suheng.structure.view.activity;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.PathParser;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.suheng.structure.view.ChinaMapView;
import com.suheng.structure.view.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SVGActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svg);


        /*AnimationDrawable drawable = (AnimationDrawable) ContextCompat.getDrawable(this, R.drawable.map_my_location_img);
        //drawable.start();
        ImageView imageView = findViewById(R.id.view_image);
        imageView.setImageDrawable(drawable);
        drawable.start();*/

        ImageView imageView = findViewById(R.id.image_svg);
        AnimatedVectorDrawableCompat vectorDrawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.water_drop_anim);
        vectorDrawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.tt_search_anim);
        if (vectorDrawableCompat != null) {
            imageView.setImageDrawable(vectorDrawableCompat);
            vectorDrawableCompat.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                @Override
                public void onAnimationStart(Drawable drawable) {
                    super.onAnimationStart(drawable);
                    Log.d("Wbj", "-----svg anim start-----");
                }

                @Override
                public void onAnimationEnd(Drawable drawable) {
                    super.onAnimationEnd(drawable);
                    Log.d("Wbj", "-----svg anim end-----");
                }
            });

            vectorDrawableCompat.start();
        }

        ImageView imageView2 = findViewById(R.id.image_svg2);
        //imageView2.setImageBitmap(BitmapHelper.get(this, R.drawable.tt_search_colors, Color.BLACK));

        this.getPathList();

        imageView2.setImageBitmap(get(this, R.drawable.tt_search_colors, 1));
    }

    private static final String NAME_SPACE = "http://schemas.android.com/apk/res/android";
    private final List<ChinaMapView.PathBean> mPathBeans = new ArrayList<>();
    private final RectF mRectF = new RectF();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final String TAG = "Wbj";

    private void getPathList() {
        try {
            Drawable fromXml = DrawableWrapper.createFromXml(getResources(), getResources().getXml(R.xml.tt_search_colors));
            Log.i(TAG, "fromXml: " + fromXml.getIntrinsicWidth() + ", " + fromXml.getIntrinsicHeight());
        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, "fromXml error", e);
        }

        XmlResourceParser parser = getResources().getXml(R.xml.tt_search_colors);
        try {
            ChinaMapView.PathBean pathBean;
            RectF rectF = new RectF();
            float left = -1, right = -1, top = -1, bottom = -1;

            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();
                    Log.i(TAG, "start tagName: " + tagName);
                    if ("vector".equals(tagName)) {
                        String width = parser.getAttributeValue(NAME_SPACE, "width");
                        String height = parser.getAttributeValue(NAME_SPACE, "height");
                        String viewportWidth = parser.getAttributeValue(NAME_SPACE, "viewportWidth");
                        String viewportHeight = parser.getAttributeValue(NAME_SPACE, "viewportHeight");
                        Log.d(TAG, "vector: " + width + ", " + height + ", " + viewportWidth + ", " + viewportHeight);
                    }

                    if ("path".equals(tagName)) {
                        pathBean = new ChinaMapView.PathBean();

                        pathBean.setName(parser.getAttributeValue(NAME_SPACE, "name"));
                        pathBean.setFillColor(parser.getAttributeValue(NAME_SPACE, "fillColor"));
                        pathBean.setStrokeColor(parser.getAttributeValue(NAME_SPACE, "strokeColor"));
                        pathBean.setStrokeWidth(parser.getAttributeValue(NAME_SPACE, "strokeWidth"));
                        Path path = PathParser.createPathFromPathData(parser.getAttributeValue(NAME_SPACE, "pathData"));
                        pathBean.setPath(path);

                        mPathBeans.add(pathBean);

                        rectF.setEmpty();
                        path.computeBounds(rectF, true);
                        Log.d(TAG, "path, computeBounds: " + rectF.toShortString());
                        left = ((left == -1) ? rectF.left : Math.min(left, rectF.left));
                        top = ((top == -1) ? rectF.top : Math.min(top, rectF.top));
                        right = ((right == -1) ? rectF.right : Math.max(right, rectF.right));
                        bottom = ((bottom == -1) ? rectF.bottom : Math.max(bottom, rectF.bottom));
                    }
                }

                if (parser.getEventType() == XmlPullParser.END_TAG) {
                    String tagName = parser.getName();
                    Log.v(TAG, "end tagName: " + tagName);
                }

                parser.next();
            }

            parser.close();

            mRectF.set(left, top, right, bottom);
            Log.i(TAG, "path, RectF: " + mRectF.toShortString() + ", width: " + mRectF.width() + ", " + mRectF.height());

            /*mListSize = mPathBeans.size();
            if (mListSize > 0) {
                measure(getMeasuredWidth(), getMeasuredHeight());
                postInvalidate();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap get(Context context, @DrawableRes int resId/*, int color*/, float scale) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        if (drawable == null) {
            return null;
        }

        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        float density = getResources().getDisplayMetrics().density;
        float densityDpi = getResources().getDisplayMetrics().densityDpi;
        Log.i(TAG, "get: " + intrinsicWidth + ", " + intrinsicHeight + ", " + density + ", " + densityDpi);
        //Bitmap bitmap = Bitmap.createBitmap((int) (intrinsicWidth * scale), (int) (intrinsicHeight * scale), Bitmap.Config.ARGB_8888);
        //Bitmap bitmap = Bitmap.createBitmap((int) (mRectF.width() * density), (int) (mRectF.height() * density), Bitmap.Config.ARGB_8888);
        Bitmap bitmap = Bitmap.createBitmap((int) mRectF.width(), (int) mRectF.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG));
        for (ChinaMapView.PathBean pathBean : mPathBeans) {
            mPaint.setColor(Color.parseColor(pathBean.getStrokeColor()));
            canvas.drawPath(pathBean.getPath(), mPaint);
        }
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;

    }

}
