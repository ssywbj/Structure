package com.suheng.structure.database;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.icu.text.TimeZoneNames;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MatrixTest {
    private static final String TAG = "Wbj";

    @Test
    public void textUnitMatrix() {
        Matrix matrix = new Matrix();//新建一个矩阵，默认是单位矩阵
        Log.d(TAG, "unit matrix: " + matrix.toString() + "\n" + matrix.toShortString());

        TimeZone timeZone = TimeZone.getDefault();
        Log.d(TAG, "displayName: " + timeZone.getDisplayName() + ", id: " + timeZone.getID());
        TimeZoneNames zoneNames = TimeZoneNames.getInstance(Locale.getDefault());
        Log.d(TAG, "getExemplarLocationName: " + zoneNames.getExemplarLocationName(timeZone.getID()));
    }

    @Test
    public void textMapPoints() {
        //三个点 (0, 0)、(80, 100)、(400, 300)
        float[] points = {0, 0, 80, 100, 400, 300};
        Log.d(TAG, "points变换之前: " + Arrays.toString(points));

        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 1.1f);//x坐标缩小0.5倍，y坐标放大1.1倍
        matrix.mapPoints(points);
        Log.d(TAG, "points变换之后: " + Arrays.toString(points));

        float[] src = {0, 0, 80, 100, 400, 300};
        float[] dst = new float[6];
        Log.d(TAG, "dst变换之前: " + Arrays.toString(dst));
        matrix.mapPoints(dst, src);
        Log.d(TAG, "dst变换之后: " + Arrays.toString(dst));

        src = new float[]{0, 0, 80, 100, 400, 300};
        dst = new float[6];
        //int dstIndex：从目标数组的第几个开始写入
        //int srcIndex：从源数组第几个开始变换；int pointCount：变换的点的个数（一个点用两个数值表示）
        matrix.mapPoints(dst, 0, src, 2, 1);
        Log.d(TAG, "dst变换之后: " + Arrays.toString(dst));
    }

    @Test
    public void textMapRadius() {
        float radius = 100;
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 1f);//x坐标缩放0.5
        //mapRadius：测量半径。由于圆可能会因为画布变换变成椭圆，所以测量的是平均半径。
        float result = matrix.mapRadius(radius);
        Log.d(TAG, "mapRadius: " + result);
    }

    @Test
    public void textMapRect() {
        RectF rectF = new RectF(100, 100, 500, 600);
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.8f);
        boolean result = matrix.mapRect(rectF);//变换之后是否还是矩形
        Log.d(TAG, "result: " + result + ", " + rectF.toString());

        RectF dst = new RectF();
        RectF src = new RectF(100, 100, 500, 600);
        matrix.reset();
        matrix.setScale(0.5f, 0.6f);
        matrix.postSkew(0.5f, 1f);
        result = matrix.mapRect(dst, src);
        Log.d(TAG, "result: " + result + ", " + dst.toString());
    }

    @Test
    public void textPrePost() {
        float[] src = new float[]{20, 20, 300, 300};
        float[] dst = new float[src.length];
        Matrix matrix = new Matrix();
        Log.d(TAG, "unit matrix: " + matrix.toShortString());
        Log.d(TAG, "src points: " + Arrays.toString(src));

        Log.d(TAG, "scale----------------------------------------------------------------");
        matrix.preScale(0.4f, 1.1f);
        Log.d(TAG, "pre scale matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.d(TAG, "pre scale points: " + Arrays.toString(dst));
        matrix.reset();
        matrix.postScale(0.4f, 1.1f);
        Log.d(TAG, "post scale matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.d(TAG, "post scale points: " + Arrays.toString(dst));

        Log.d(TAG, "----------------------------------------------------------------");
        matrix.reset();
        matrix.preTranslate(4f, 7f);
        Log.d(TAG, "pre translate matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.d(TAG, "pre translate points: " + Arrays.toString(dst));
        matrix.reset();
        matrix.postTranslate(4f, 7f);
        Log.d(TAG, "post translate matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.d(TAG, "post translate points: " + Arrays.toString(dst));

        Log.d(TAG, "----------------------------------------------------------------");
        matrix.reset();
        matrix.preRotate(30);
        Log.d(TAG, "pre rotate matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.d(TAG, "pre rotate points: " + Arrays.toString(dst));
        matrix.reset();
        matrix.postRotate(30);
        Log.d(TAG, "post rotate matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.d(TAG, "post rotate points: " + Arrays.toString(dst));

        Log.d(TAG, "----------------------------------------------------------------");
        matrix.reset();
        matrix.preSkew(3, 2);
        Log.d(TAG, "pre skew matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.d(TAG, "pre skew points: " + Arrays.toString(dst));
        matrix.reset();
        matrix.postSkew(3, 2);
        Log.d(TAG, "post skew matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.d(TAG, "post skew points: " + Arrays.toString(dst));
    }

}