package com.suheng.structure.view;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MatrixTest {
    private static final String TAG = MatrixTest.class.getSimpleName();

    @Test
    public void testUnitMatrix() {
        Matrix matrix = new Matrix();//新建一个矩阵，默认是单位矩阵
        Log.d(TAG, "unit matrix: " + matrix.toString() + "\n" + matrix.toShortString());
    }

    @Test
    public void testMapPoints() {
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
    public void testMapRadius() {
        float radius = 100;
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 1f);//x坐标缩放0.5
        //mapRadius：测量半径。由于圆可能会因为画布变换变成椭圆，所以测量的是平均半径。
        float result = matrix.mapRadius(radius);
        Log.d(TAG, "mapRadius: " + result);
    }

    @Test
    public void testPrePost() {
        float[] src = new float[]{20, 20, 300, 300};
        float[] dst = new float[src.length];
        Matrix matrix = new Matrix();
        Log.d(TAG, "unit matrix: " + matrix.toShortString());
        Log.d(TAG, "src points: " + Arrays.toString(src));

        Log.i(TAG, "Scale---------------------------------------------------------------");
        matrix.preScale(0.4f, 1.1f);
        Log.d(TAG, "pre scale matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.d(TAG, "pre scale points: " + Arrays.toString(dst));
        matrix.reset();
        matrix.postScale(0.4f, 1.1f);
        Log.i(TAG, "post scale matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.i(TAG, "post scale points: " + Arrays.toString(dst));
        matrix.setScale(0.4f, 1.1f);
        Log.v(TAG, "scale matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.v(TAG, "scale points: " + Arrays.toString(dst));

        Log.d(TAG, "Translate-----------------------------------------------------------");
        matrix.reset();
        matrix.preTranslate(4f, 7f);
        Log.d(TAG, "pre translate matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.d(TAG, "pre translate points: " + Arrays.toString(dst));
        matrix.reset();
        matrix.postTranslate(4f, 7f);
        Log.i(TAG, "post translate matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.i(TAG, "post translate points: " + Arrays.toString(dst));
        matrix.setTranslate(4f, 7f);
        Log.v(TAG, "set translate matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.v(TAG, "set translate points: " + Arrays.toString(dst));

        Log.d(TAG, "Rotate----------------------------------------------------------------");
        matrix.reset();
        matrix.preRotate(30);
        Log.d(TAG, "pre rotate matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.d(TAG, "pre rotate points: " + Arrays.toString(dst));
        matrix.reset();
        matrix.postRotate(30);
        Log.i(TAG, "post rotate matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.i(TAG, "post rotate points: " + Arrays.toString(dst));
        matrix.setRotate(30);
        Log.v(TAG, "set rotate matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.v(TAG, "set rotate points: " + Arrays.toString(dst));

        Log.d(TAG, "Skew----------------------------------------------------------------");
        matrix.reset();
        matrix.preSkew(3, 2);
        Log.d(TAG, "pre skew matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.d(TAG, "pre skew points: " + Arrays.toString(dst));
        matrix.reset();
        matrix.postSkew(3, 2);
        Log.i(TAG, "post skew matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.i(TAG, "post skew points: " + Arrays.toString(dst));
        matrix.setSkew(3, 2);
        Log.v(TAG, "set skew matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.v(TAG, "set skew points: " + Arrays.toString(dst));

        //单个变换，pre、post、set的结果都一样
    }

    @Test
    public void testMapRect() {
        RectF rectF = new RectF(100, 100, 500, 600);
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.8f);
        Log.v(TAG, "set scale matrix: " + matrix.toShortString());
        boolean result = matrix.mapRect(rectF);//变换之后是否还是矩形
        Log.d(TAG, "scale result: " + result + ", " + rectF.toString());

        rectF = new RectF(100, 100, 500, 600);

        RectF dst = new RectF();
        matrix.reset();
        matrix.setScale(0.5f, 0.8f);
        matrix.postTranslate(20, 30);
        Log.v(TAG, "setScale postTranslate matrix: " + matrix.toShortString());
        result = matrix.mapRect(dst, rectF);
        Log.i(TAG, "setScale postTranslate result: " + result + ", " + dst.toString());

        matrix.reset();
        matrix.setScale(0.5f, 0.8f);
        matrix.preTranslate(20, 30);
        Log.d(TAG, "setScale preTranslate matrix: " + matrix.toShortString());
        result = matrix.mapRect(dst, rectF);
        Log.v(TAG, "setScale preTranslate result: " + result + ", " + dst.toString());

        //复合变换：后乘、前乘结果不一致
    }

    @Test
    public void testComplexMulti() {
        RectF src = new RectF(100, 100, 500, 600);
        RectF dst = new RectF();

        Log.w(TAG, "pre post---------------------------------------------");
        Matrix matrix = new Matrix();
        matrix.preScale(0.5f, 0.8f);
        matrix.mapRect(dst, src);
        Log.i(TAG, "preScale result: " + dst.toString());
        matrix.postTranslate(20, 30);
        Log.v(TAG, "preScale postTranslate matrix: " + matrix.toShortString());
        matrix.mapRect(dst, src);
        Log.d(TAG, "preScale postTranslate result: " + dst.toString());

        Log.w(TAG, "post pre---------------------------------------------");
        matrix.reset();
        matrix.postScale(0.5f, 0.8f);
        matrix.mapRect(dst, src);
        Log.i(TAG, "postScale result: " + dst.toString());
        matrix.preTranslate(20, 30);
        Log.v(TAG, "postScale preTranslate matrix: " + matrix.toShortString());
        matrix.mapRect(dst, src);
        Log.d(TAG, "postScale preTranslate result: " + dst.toString());

        Log.w(TAG, "post post--------------------------------------------");
        matrix.reset();
        matrix.postScale(0.5f, 0.8f);
        matrix.mapRect(dst, src);
        Log.i(TAG, "postScale result: " + dst.toString());
        matrix.postTranslate(20, 30);
        Log.v(TAG, "postScale postTranslate matrix: " + matrix.toShortString());
        matrix.mapRect(dst, src);
        Log.d(TAG, "postScale postTranslate result: " + dst.toString());

        Log.w(TAG, "pre pre----------------------------------------------");
        matrix.reset();
        matrix.preScale(0.5f, 0.8f);
        matrix.mapRect(dst, src);
        Log.i(TAG, "preScale result: " + dst.toString());
        matrix.preTranslate(20, 30);
        Log.v(TAG, "preScale preTranslate matrix: " + matrix.toShortString());
        matrix.mapRect(dst, src);
        Log.d(TAG, "preScale preTranslate result: " + dst.toString());

        //pre、post与post、post结果一样，post、pre与pre、pre结果一样
        //感觉pre、post与字面理解效果一样，如："preScale(0.5f, 0.8f)，postTranslate(20, 30)"按字面理解就
        //是先缩放括号里面的指定数值再平移括号里面的指定数值，想法可能不正确，持续探究中
    }

}