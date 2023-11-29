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
        Log.d(TAG, "unit matrix: " + matrix + "\n" + matrix.toShortString());
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
        float[] dst = new float[src.length];
        Log.d(TAG, "dst变换之前: " + Arrays.toString(dst));
        matrix.mapPoints(dst, src);
        Log.d(TAG, "dst变换之后: " + Arrays.toString(dst));

        src = new float[]{0, 0, 80, 100, 400, 300};
        dst = new float[src.length];
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

    //前乘后乘都是以原最新结果的矩阵为参照，前乘表示它放在乘号前面做为被乘数，后乘表示它放在乘号后面做为乘数
    @Test
    public void testPrePost() {
        float[] src = new float[]{20, 20, 300, 300};
        float[] dst = new float[src.length];
        Matrix matrix = new Matrix();
        Log.d(TAG, "unit matrix: " + matrix.toShortString());
        Log.d(TAG, "src points: " + Arrays.toString(src));

        Log.i(TAG, "Scale---------------------------------------------------------------");
        matrix.preScale(0.4f, 1.1f);
        Log.d(TAG, "preScale matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.d(TAG, "preScale points: " + Arrays.toString(dst));
        matrix.reset();
        matrix.postScale(0.4f, 1.1f);
        Log.i(TAG, "postScale matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.i(TAG, "postScale points: " + Arrays.toString(dst));
        matrix.setScale(0.4f, 1.1f);
        Log.v(TAG, "setScale matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.v(TAG, "setScale points: " + Arrays.toString(dst));

        Log.d(TAG, "Translate-----------------------------------------------------------");
        matrix.reset();
        matrix.preTranslate(4f, 7f);
        Log.d(TAG, "preTranslate matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.d(TAG, "preTranslate points: " + Arrays.toString(dst));
        matrix.reset();
        matrix.postTranslate(4f, 7f);
        Log.i(TAG, "postTranslate matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.i(TAG, "postTranslate points: " + Arrays.toString(dst));
        matrix.setTranslate(4f, 7f);
        Log.v(TAG, "setTranslate matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.v(TAG, "setTranslate points: " + Arrays.toString(dst));

        Log.d(TAG, "Rotate----------------------------------------------------------------");
        matrix.reset();
        matrix.preRotate(30);
        Log.d(TAG, "preRotate matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.d(TAG, "preRotate points: " + Arrays.toString(dst));
        matrix.reset();
        matrix.postRotate(30);
        Log.i(TAG, "postRotate matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.i(TAG, "postRotate points: " + Arrays.toString(dst));
        matrix.setRotate(30);
        Log.v(TAG, "setRotate matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.v(TAG, "setRotate points: " + Arrays.toString(dst));

        Log.d(TAG, "Skew----------------------------------------------------------------");
        matrix.reset();
        matrix.preSkew(3, 2);
        Log.d(TAG, "preSkew matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.d(TAG, "preSkew points: " + Arrays.toString(dst));
        matrix.reset();
        matrix.postSkew(3, 2);
        Log.i(TAG, "postSkew matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.i(TAG, "postSkew points: " + Arrays.toString(dst));
        matrix.setSkew(3, 2);
        Log.v(TAG, "setSkew matrix: " + matrix.toShortString());
        matrix.mapPoints(dst, src);
        Log.v(TAG, "setSkew points: " + Arrays.toString(dst));

        //pre：前乘，post：后乘。
        //当一个矩阵与单位矩阵做乘法运算时，满足乘法交换律，所以单个变换时前乘后乘结果都一样；set是直接去改变矩阵的值，不做运算
    }

    @Test
    public void testMapRect() {
        RectF rectF = new RectF(100, 100, 500, 600);
        Log.i(TAG, "origin rect: " + rectF);

        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.8f);
        Log.v(TAG, "set scale matrix: " + matrix.toShortString());
        boolean result = matrix.mapRect(rectF);//变换之后是否还是矩形
        Log.d(TAG, "scale result: " + result + ", " + rectF);

        rectF = new RectF(100, 100, 500, 600);

        RectF dst = new RectF();
        matrix.reset();
        matrix.setScale(0.5f, 0.8f);
        matrix.postTranslate(20, 30);
        Log.v(TAG, "setScale postTranslate matrix: " + matrix.toShortString());
        result = matrix.mapRect(dst, rectF);
        Log.i(TAG, "setScale postTranslate result: " + result + ", " + dst);

        matrix.reset();
        matrix.setScale(0.5f, 0.8f);
        matrix.preTranslate(20, 30);
        Log.d(TAG, "setScale preTranslate matrix: " + matrix.toShortString());
        result = matrix.mapRect(dst, rectF);
        Log.v(TAG, "setScale preTranslate result: " + result + ", " + dst);

        //复合变换：非单位矩阵的后乘、前乘结果不一致（即就是平时所说的矩阵乘法不满足乘法交换率）
    }

    //https://zh.wikipedia.org/wiki/%E7%9F%A9%E9%99%A3%E4%B9%98%E6%B3%95
    //https://www.cnblogs.com/ljy-endl/p/11411665.html
    //https://baike.baidu.com/item/%E7%9F%A9%E9%98%B5%E4%B9%98%E6%B3%95/5446029?fr=aladdin
    @Test
    public void testComplexMulti() {
        RectF src = new RectF(100, 100, 500, 600);
        Log.i(TAG, "origin rect: " + src);
        RectF dst = new RectF();

        Log.w(TAG, "pre post---------------------------------------------");
        Matrix matrix = new Matrix();
        matrix.preScale(0.5f, 0.8f);
        matrix.mapRect(dst, src);
        Log.i(TAG, "preScale result: " + dst);
        matrix.postTranslate(20, 30); //translate矩阵乘以前面scale后的矩阵
        Log.v(TAG, "preScale postTranslate matrix: " + matrix.toShortString());
        matrix.mapRect(dst, src);
        Log.d(TAG, "preScale postTranslate result: " + dst);

        Log.w(TAG, "post pre---------------------------------------------");
        matrix.reset();
        matrix.postScale(0.5f, 0.8f);
        matrix.mapRect(dst, src);
        Log.i(TAG, "postScale result: " + dst);
        matrix.preTranslate(20, 30); //前面scale后的矩阵乘以translate矩阵
        Log.v(TAG, "postScale preTranslate matrix: " + matrix.toShortString());
        matrix.mapRect(dst, src);
        Log.d(TAG, "postScale preTranslate result: " + dst);

        Log.w(TAG, "post post--------------------------------------------");
        matrix.reset();
        matrix.postScale(0.5f, 0.8f);
        matrix.mapRect(dst, src);
        Log.i(TAG, "postScale result: " + dst);
        matrix.postTranslate(20, 30);
        Log.v(TAG, "postScale postTranslate matrix: " + matrix.toShortString());
        matrix.mapRect(dst, src);
        Log.d(TAG, "postScale postTranslate result: " + dst);

        Log.w(TAG, "pre pre----------------------------------------------");
        matrix.reset();
        matrix.preScale(0.5f, 0.8f);
        matrix.mapRect(dst, src);
        Log.i(TAG, "preScale result: " + dst);
        matrix.preTranslate(20, 30);
        Log.v(TAG, "preScale preTranslate matrix: " + matrix.toShortString());
        matrix.mapRect(dst, src);
        Log.d(TAG, "preScale preTranslate result: " + dst);

        //pre、post与post、post结果一样，post、pre与pre、pre结果一样
    }

}