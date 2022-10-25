package com.suheng.structure.view.activity;

import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.XmlRes;
import androidx.appcompat.app.AppCompatActivity;
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

public class SVGDemoActivity extends AppCompatActivity {

    //https://xianxiaotao.github.io/2019/07/05/05%20CUSTOM%20VIEW/05.2%20%E7%B2%BE%E9%80%9A%E8%87%AA%E5%AE%9A%E4%B9%89%20View%20%E4%B9%8B%E5%8A%A8%E7%94%BB%E8%BF%9B%E9%98%B6%E2%80%94%E2%80%94SVG%20%E5%8A%A8%E7%94%BB/
    //https://blog.csdn.net/EthanCo/article/details/104092794
    //https://developer.android.com/guide/topics/graphics/vector-drawable-resources?hl=zh-cn
    //https://blog.csdn.net/zyjzyj2/article/details/53530568
    //https://juejin.cn/post/6844903524103356430
    //https://www.jianshu.com/p/456df1434739
    //https://maronyea.me/dev/304/
    //https://svga.io/svga-preview.html

    //https://juejin.cn/post/6844903523696525325
    //https://www.cnblogs.com/yuhanghzsd/p/5466846.html
    //https://blog.csdn.net/weixin_41620505/article/details/107255168
    //https://www.jianshu.com/p/4707a4738a51
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svg_demo);

        ImageView imageView = findViewById(R.id.image_svg);
        //final AnimatedVectorDrawableCompat vectorDrawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.checkbox_checked_animated);
        //final AnimatedVectorDrawableCompat vectorDrawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.checkbox_checked_animated2);
        final AnimatedVectorDrawableCompat vectorDrawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.wbj_svg1_animated);
        imageView.setImageDrawable(vectorDrawableCompat);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vectorDrawableCompat != null) {
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

                    if (vectorDrawableCompat.isRunning()) {
                        vectorDrawableCompat.jumpToCurrentState();
                    } else {
                        vectorDrawableCompat.start();
                    }
                }
            }
        });
        ImageView imageViewRev = findViewById(R.id.image_svg_rev);
        AnimatedVectorDrawableCompat drawableRev = AnimatedVectorDrawableCompat.create(this, R.drawable.checkbox_unchecked_animated);
        imageViewRev.setImageDrawable(drawableRev);
        imageViewRev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawableRev != null) {
                    drawableRev.start();
                }
            }
        });

        ImageView imageWaterDrop = findViewById(R.id.image_water_drop);
        Drawable drawableWaterDrop = imageWaterDrop.getDrawable();
        if (drawableWaterDrop instanceof Animatable) {
            ((Animatable) drawableWaterDrop).start();
        }
        imageWaterDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = imageWaterDrop.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
            }
        });

        ImageView imageLoading = findViewById(R.id.image_loading);
        //imageLoading.setImageBitmap(BitmapHelper.get(this, R.drawable.loading2, Color.BLACK));
        this.getPathList();
        try {
            /*Drawable drawableSearch = Drawable.createFromXml(getResources(), getResources().getXml(R.xml.loading2));
            Log.i(TAG, "from xml, drawableSearch: " + drawableSearch.getIntrinsicWidth() + ", " + drawableSearch.getIntrinsicHeight());
            imageLoading.setImageDrawable(drawableSearch);*/

            imageLoading.setImageDrawable(AnimatedVectorDrawableCompat.create(this, R.drawable.loading_animated));
            imageLoading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Drawable drawable = imageLoading.getDrawable();
                    if (drawable instanceof Animatable) {
                        ((Animatable) drawable).start();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "from xml error", e);
        }
        //imageLoading.setImageBitmap(get(R.xml.tt_search_colors, 1));

        /*ImageView imageSearch = findViewById(R.id.image_search);
        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = imageSearch.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
            }
        });*/
    }

    private static final String NAME_SPACE = "http://schemas.android.com/apk/res/android";
    private final List<ChinaMapView.PathBean> mPathBeans = new ArrayList<>();
    private final RectF mRectF = new RectF();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final String TAG = "Wbj";

    private void getPathList() {
        XmlResourceParser parser = getResources().getXml(R.xml.loading2);
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


    public Bitmap get(@XmlRes int xmlId, float scale) {
        Drawable drawable = null;
        try {
            drawable = Drawable.createFromXml(getResources(), getResources().getXml(xmlId));
        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, "drawable from xml error", e);
        }

        if (drawable == null) {
            return null;
        }

        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        Log.i(TAG, "drawable from xml: " + intrinsicWidth + ", " + intrinsicHeight);
        Bitmap bitmap = Bitmap.createBitmap((int) (intrinsicWidth * scale), (int) (intrinsicHeight * scale), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG));
        for (ChinaMapView.PathBean pathBean : mPathBeans) {
            mPaint.setColor(Color.parseColor(pathBean.getStrokeColor()));
            canvas.drawPath(pathBean.getPath(), mPaint);
        }
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public Bitmap get(@XmlRes int xmlId) {
        return this.get(xmlId, 1);
    }

}
