package com.suheng.wallpaper.myhealth;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.SurfaceHolder;

import androidx.core.content.ContextCompat;

import com.suheng.wallpaper.basic.utils.DimenUtil;
import com.suheng.wallpaper.basic.service.CanvasWallpaperService;
import com.suheng.wallpaper.basic.utils.DateUtil;

import java.util.Calendar;

public class MyHealthWatchFace extends CanvasWallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new LiveEngine();
    }

    private final class LiveEngine extends CanvasEngine {
        private static final int SCALES = 6;
        private static final float SCALE_DEGREES = 1.0f * 360 / SCALES;
        private final PointF mPointScreenCenter = new PointF();//屏幕中心点
        private float mRadiusOuter, mRadiusInner;

        private Paint mPaint;
        private final RectF mRectF = new RectF();

        private int mLittleTriangleHeight;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            mPointScreenCenter.x = 1.0f * width / 2;//屏幕中心X坐标
            mPointScreenCenter.y = 1.0f * height / 2;//屏幕中心Y坐标
            mRadiusOuter = Math.min(mPointScreenCenter.x, mPointScreenCenter.y)
                    - DimenUtil.dip2px(mContext, 1);
            mRadiusInner = mRadiusOuter - DimenUtil.dip2px(mContext, 36);

            invalidate();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                registerRunnableSecondTicker();
            } else {
                unregisterRunnableSecondTicker();
            }
        }

        @Override
        public void updateTime() {
            Calendar calendar = Calendar.getInstance();

            mHour = DateUtil.getHour(mContext);
            mMinute = calendar.get(Calendar.MINUTE);
            mSecond = calendar.get(Calendar.SECOND);
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawColor(ContextCompat.getColor(mContext, R.color.basic_wallpaper_bg_black));//画面背景
            mPaint.setStyle(Paint.Style.STROKE);

            float radius = mRadiusInner - DimenUtil.dip2px(mContext, 8);
            mRectF.set(mPointScreenCenter.x - radius, mPointScreenCenter.y - radius
                    , mPointScreenCenter.x + radius, mPointScreenCenter.y + radius);
            //canvas.drawRect(mRectF, mPaint);
            canvas.drawBitmap(mBitmapManager.get(R.drawable.my_health_stripe_bg), null, mRectF, null);

            float strokeWidth = 1f;
            //-90：从矩形区域顶边中点开始(-110在它的左侧)；0：从矩形区域右边中点开始；90：从矩形区域底边中点开始；180：从矩形区域左边中点开始
            float startAngle, sweepAngle;
            for (int index = 0; index < SCALES; index++) {
                canvas.save();
                canvas.rotate(index * SCALE_DEGREES, mPointScreenCenter.x, mPointScreenCenter.y);

                //外弧
                mPaint.setStrokeWidth(strokeWidth * 1.5f);
                mPaint.setColor(Color.parseColor("#1E1E1E"));
                startAngle = -114;
                sweepAngle = (Math.abs(startAngle) - Math.abs(-90)) * 2;
                //在这个矩形区域里画弧
                mRectF.set(mPointScreenCenter.x - mRadiusOuter, mPointScreenCenter.y - mRadiusOuter
                        , mPointScreenCenter.x + mRadiusOuter, mPointScreenCenter.y + mRadiusOuter);
                canvas.drawArc(mRectF, startAngle, sweepAngle, false, mPaint);

                //内弧
                if (index != 5) {
                    mPaint.setStrokeWidth(strokeWidth * 2);
                    mPaint.setColor(Color.parseColor("#656565"));
                    startAngle = -112;
                    sweepAngle = (Math.abs(startAngle) - Math.abs(-90)) * 2;
                    mRectF.set(mPointScreenCenter.x - mRadiusInner, mPointScreenCenter.y - mRadiusInner
                            , mPointScreenCenter.x + mRadiusInner, mPointScreenCenter.y + mRadiusInner);
                    canvas.drawArc(mRectF, startAngle, sweepAngle, false, mPaint);
                }
                canvas.restore();
            }

            Bitmap bitmap = mBitmapManager.get(R.drawable.my_health_little_triangle
                    , ContextCompat.getColor(mContext, R.color.my_health_little_triangle));
            mLittleTriangleHeight = bitmap.getHeight();
            float degrees;
            for (int index = 0; index < SCALE_DEGREES * 2; index++) {
                canvas.save();
                degrees = index * SCALE_DEGREES / 2;
                canvas.rotate(degrees, mPointScreenCenter.x, mPointScreenCenter.y);
                if (degrees % 60 != 0) {
                    canvas.drawBitmap(bitmap, mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2
                            , mPointScreenCenter.y - mRadiusInner, null);

                    /*canvas.drawLine(mPointScreenCenter.x, mPointScreenCenter.y
                            , mPointScreenCenter.x, mPointScreenCenter.y - mRadiusOuter, mPaint);*/
                }
                canvas.restore();
            }

            this.paintTime(canvas);
            this.paintIconInfo(canvas);
        }

        private void paintTime(Canvas canvas) {
            //分钟
            int color = Color.WHITE;
            int units = mMinute % 10;//个位
            int tens = mMinute / 10;//十位
            Bitmap bitmap = mBitmapManager.get(getNumberResId(tens), color);
            float left = mPointScreenCenter.x - bitmap.getWidth();
            float hourBitmapHeight = 1.0f * bitmap.getHeight() / 2;
            float top = mPointScreenCenter.y - hourBitmapHeight;
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(getNumberResId(units), color);
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(R.drawable.my_health_minute_flag, color);
            canvas.drawBitmap(bitmap, left, top, null);

            //秒钟
            color = ContextCompat.getColor(mContext, R.color.second_number);
            units = mSecond % 10;//个位
            tens = mSecond / 10;//十位
            left = mPointScreenCenter.x + mRadiusInner / 2;
            top = mPointScreenCenter.y + hourBitmapHeight;
            bitmap = mBitmapManager.get(getNumberResId(tens), color);
            left -= DimenUtil.dip2px(mContext, 1);
            top -= (bitmap.getHeight() + 2);
            canvas.drawBitmap(bitmap, left, top, null);

            left += (bitmap.getWidth());
            bitmap = mBitmapManager.get(getNumberResId(units), color);
            canvas.drawBitmap(bitmap, left, top, null);

            left += (bitmap.getWidth() + DimenUtil.dip2px(mContext, 2));
            bitmap = mBitmapManager.get(R.drawable.my_health_second_flag, color);
            canvas.drawBitmap(bitmap, left, top, null);

            left += (bitmap.getWidth() + DimenUtil.dip2px(mContext, 2));
            bitmap = mBitmapManager.get(R.drawable.my_health_second_flag, color);
            canvas.drawBitmap(bitmap, left, top, null);

            //时钟
            color = ContextCompat.getColor(mContext, R.color.my_health_little_triangle);
            units = mHour % 10;//个位
            tens = mHour / 10;//十位
            bitmap = mBitmapManager.get(getNumberResId(tens), color);
            left = mPointScreenCenter.x - mRadiusInner - DimenUtil.dip2px(mContext, 4);
            top = mPointScreenCenter.y - bitmap.getHeight() - mLittleTriangleHeight;
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(getNumberResId(units), color);
            canvas.drawBitmap(bitmap, left, top, null);

            int marginBottom = DimenUtil.dip2px(mContext, 2);
            float lineLeft = mPointScreenCenter.x - mRadiusInner + DimenUtil.dip2px(mContext, 10);
            int lineHeight = DimenUtil.dip2px(mContext, 4);
            float lineTop = top - (marginBottom + lineHeight);
            float lineLen = 1.0f * bitmap.getWidth() / 4;

            //黄线上方的日期
            color = Color.WHITE;
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH) + 1;
            units = month % 10;//个位
            tens = month / 10;//十位
            left = (lineLeft + DimenUtil.dip2px(mContext, 10));
            bitmap = mBitmapManager.get(getNumberResId(tens));
            top -= (bitmap.getHeight() + marginBottom + 2.6f * lineHeight);
            canvas.drawBitmap(bitmap, left, top, null);
            lineLen += bitmap.getWidth();
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(getNumberResId(units), color);
            canvas.drawBitmap(bitmap, left, top, null);
            lineLen += bitmap.getWidth();

            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(R.drawable.my_health_diagonal, color);
            canvas.drawBitmap(bitmap, left, top, null);
            lineLen += bitmap.getWidth();

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            units = day % 10;//个位
            tens = day / 10;//十位
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(getNumberResId(tens), color);
            canvas.drawBitmap(bitmap, left, top, null);
            lineLen += bitmap.getWidth();
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(getNumberResId(units), color);
            canvas.drawBitmap(bitmap, left, top, null);
            lineLen += bitmap.getWidth();

            //时钟与时间之前的黄线
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(ContextCompat.getColor(mContext, R.color.my_health_little_triangle));
            mRectF.set(lineLeft, lineTop - 1.0f * lineHeight / 2
                    , lineLeft + lineLen + DimenUtil.dip2px(mContext, 4), lineTop + 1.0f * lineHeight / 2);
            canvas.drawRoundRect(mRectF, 1.0f * lineHeight / 2, 1.0f * lineHeight / 2, mPaint);
        }

        private void paintIconInfo(Canvas canvas) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.RED);
            float radius = mRadiusInner + (mRadiusOuter - mRadiusInner) / 2;
            //canvas.drawCircle(mPointScreenCenter.x, mPointScreenCenter.y, radius, mPaint);

            final int scales = 100;
            final float scaleDegree = 1.0f * 360 / scales;
            float stopY = mPointScreenCenter.y - radius, rotateDegrees;
            Bitmap bitmap;
            int color = ContextCompat.getColor(mContext, R.color.alphabet_uppercase);
            for (int index = 0; index < scales; index++) {
                rotateDegrees = index * scaleDegree;
                bitmap = null;

                if (index > 73 && index < 94) {
                    continue;
                }

                canvas.save();
                canvas.rotate(rotateDegrees, mPointScreenCenter.x, mPointScreenCenter.y);//画布旋转后，在低分辨率手机上bitmap会有些锯齿现象，但能接受
                //canvas.drawLine(mPointScreenCenter.x, mPointScreenCenter.y, mPointScreenCenter.x, stopY, mPaint);

                if (index == 1) {//卡路里后半部分
                    bitmap = mBitmapManager.get(R.drawable.number_2, color);
                } else if (index == 3) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_k, color);
                } else if (index == 4) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_c, color);
                } else if (index == 5) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a, color);
                } else if (index == 6) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_l, color);
                } else if (index == 13) {//心率
                    bitmap = mBitmapManager.get(R.drawable.paint_heart_rate, color);
                } else if (index == 15) {
                    bitmap = mBitmapManager.get(R.drawable.number_1, color);
                } else if (index == 16) {
                    bitmap = mBitmapManager.get(R.drawable.number_0, color);
                } else if (index == 17) {
                    bitmap = mBitmapManager.get(R.drawable.number_8, color);
                } else if (index == 19) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_b, color);
                } else if (index == 20) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_p, color);
                } else if (index == 21) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_m, color);
                } else if (index == 27) {//跑步
                    bitmap = mBitmapManager.get(R.drawable.number_2, color, 0.2f, 180);
                } else if (index == 28) {
                    bitmap = mBitmapManager.get(R.drawable.number_1, color, 0.2f, 180);
                } else if (index == 29) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_my_health_diagonal, color);
                } else if (index == 30) {
                    bitmap = mBitmapManager.get(R.drawable.number_8, color, 0.2f, 180);
                } else if (index == 31) {
                    bitmap = mBitmapManager.get(R.drawable.number_0, color, 0.2f, 180);
                } else if (index == 33) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_workout, color);
                } else if (index == 35) {//sport
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color, 1, 180);
                } else if (index == 36) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_r, color, 1, 180);
                } else if (index == 37) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_o, color, 1, 180);
                } else if (index == 38) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_p, color, 1, 180);
                } else if (index == 39) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color, 1, 180);
                } else if (index == 44) {//步数
                    bitmap = mBitmapManager.get(R.drawable.number_0, color, 0.2f, 180);
                } else if (index == 45) {
                    bitmap = mBitmapManager.get(R.drawable.number_4, color, 0.2f, 180);
                } else if (index == 46) {
                    bitmap = mBitmapManager.get(R.drawable.number_9, color, 0.2f, 180);
                } else if (index == 47) {
                    bitmap = mBitmapManager.get(R.drawable.number_1, color, 0.2f, 180);
                } else if (index == 48) {
                    bitmap = mBitmapManager.get(R.drawable.number_4, color, 0.2f, 180);
                } else if (index == 49) {
                    bitmap = mBitmapManager.get(R.drawable.number_7, color, 0.2f, 180);
                } else if (index == 51) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_steps, color);
                } else if (index == 53) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color, 1, 180);
                } else if (index == 54) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_p, color, 1, 180);
                } else if (index == 55) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color, 1, 180);
                } else if (index == 56) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color, 1, 180);
                } else if (index == 57) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color, 1, 180);
                } else if (index == 61) {//电量
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_sign_percentage, color);
                } else if (index == 62) {
                    bitmap = mBitmapManager.get(R.drawable.number_0, color, 0.5f, 180);
                } else if (index == 63) {
                    bitmap = mBitmapManager.get(R.drawable.number_0, color, 0.5f, 180);
                } else if (index == 64) {
                    bitmap = mBitmapManager.get(R.drawable.number_1, color, 0.5f, 180);
                } else if (index == 66) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_battary, color, 0.5f, 180);
                } else if (index == 68) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_y, color, 1, 180);
                } else if (index == 69) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_g, color, 1, 180);
                } else if (index == 70) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_r, color, 1, 180);
                } else if (index == 71) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color, 1, 180);
                } else if (index == 72) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n, color, 1, 180);
                } else if (index == 73) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color, 1, 180);
                } else if (index == 95) {//卡路里前半部分
                    bitmap = mBitmapManager.get(R.drawable.paint_calorie, color);
                    //bitmap = mBitmapManager.get(R.drawable.paint_calorie_bak, color);
                    //bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.paint_calorie_bak);
                } else if (index == 97) {
                    bitmap = mBitmapManager.get(R.drawable.number_0, color);
                } else if (index == 98) {
                    bitmap = mBitmapManager.get(R.drawable.number_5, color);
                } else if (index == 99) {
                    bitmap = mBitmapManager.get(R.drawable.number_2, color);
                } else if (index == 0) {
                    bitmap = mBitmapManager.get(R.drawable.number_9, color);
                }

                if (bitmap != null) {
                    /*mPaint.reset();
                    mPaint.setAntiAlias(true);
                    mPaint.setColor(Color.RED);
                    canvas.drawBitmap(bitmap.extractAlpha(), mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2
                            , stopY - 1.0f * bitmap.getHeight() / 2, mPaint);*/

                    canvas.drawBitmap(bitmap, mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2
                            , stopY - 1.0f * bitmap.getHeight() / 2, null);
                }

                canvas.restore();
            }
        }
    }

    private int getNumberResId(int number) {
        switch (number) {
            case 1:
                return R.drawable.number_1;
            case 2:
                return R.drawable.number_2;
            case 3:
                return R.drawable.number_3;
            case 4:
                return R.drawable.number_4;
            case 5:
                return R.drawable.number_5;
            case 6:
                return R.drawable.number_6;
            case 7:
                return R.drawable.number_7;
            case 8:
                return R.drawable.number_8;
            case 9:
                return R.drawable.number_9;
            default:
                return R.drawable.number_0;
        }
    }

}
