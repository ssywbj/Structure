package com.suheng.wallpaper.myhealth;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.SurfaceHolder;

import androidx.core.content.ContextCompat;

import com.suheng.wallpaper.basic.DimenUtil;
import com.suheng.wallpaper.basic.service.CanvasWallpaperService;
import com.suheng.wallpaper.basic.utils.DateUtil;

import java.util.Calendar;

public class MyHealthWatchFace extends CanvasWallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new LiveEngine();
    }

    @Override
    public void updateTime() {
        Calendar calendar = Calendar.getInstance();

        mHour = DateUtil.getHour(this);
        mMinute = calendar.get(Calendar.MINUTE);
        mSecond = calendar.get(Calendar.SECOND);
    }

    private final class LiveEngine extends CanvasEngine {
        private static final int SCALES = 6;
        private static final float SCALE_DEGREES = 1.0f * 360 / SCALES;
        private final PointF mPointScreenCenter = new PointF();//屏幕中心点
        private float mRadiusOuter, mRadiusInner;

        private Paint mPaint;
        private final RectF mRectF = new RectF();

        private int mLittleTriangleHeight;

        private MyHealthBitmapManager mBitmapManager;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            mBitmapManager = new MyHealthBitmapManager(mContext);

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
                registerSecondTicker();
            } else {
                unregisterSecondTicker();
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mBitmapManager.clear();
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

            Bitmap bitmap = mBitmapManager.get(R.drawable.my_health_little_triangle, R.color.my_health_little_triangle);
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
            Calendar instance = Calendar.getInstance();
            //分钟
            int color = android.R.color.white;
            int minute = instance.get(Calendar.MINUTE);
            int units = minute % 10;//个位
            int tens = minute / 10;//十位
            Bitmap bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(tens), color);
            float left = mPointScreenCenter.x - bitmap.getWidth();
            float hourBitmapHeight = 1.0f * bitmap.getHeight() / 2;
            float top = mPointScreenCenter.y - hourBitmapHeight;
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(units), color);
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(R.drawable.my_health_minute_flag, color);
            canvas.drawBitmap(bitmap, left, top, null);

            //秒钟
            color = R.color.second_number;
            int second = instance.get(Calendar.SECOND);
            units = second % 10;//个位
            tens = second / 10;//十位
            left = mPointScreenCenter.x + mRadiusInner / 2;
            top = mPointScreenCenter.y + hourBitmapHeight;
            bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(tens), color);
            left -= DimenUtil.dip2px(mContext, 1);
            top -= (bitmap.getHeight() + 2);
            canvas.drawBitmap(bitmap, left, top, null);

            left += (bitmap.getWidth());
            bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(units), color);
            canvas.drawBitmap(bitmap, left, top, null);

            left += (bitmap.getWidth() + DimenUtil.dip2px(mContext, 2));
            bitmap = mBitmapManager.get(R.drawable.my_health_second_flag, color);
            canvas.drawBitmap(bitmap, left, top, null);

            left += (bitmap.getWidth() + DimenUtil.dip2px(mContext, 2));
            bitmap = mBitmapManager.get(R.drawable.my_health_second_flag, color);
            canvas.drawBitmap(bitmap, left, top, null);

            //时钟
            color = R.color.my_health_little_triangle;
            int hour = instance.get(Calendar.HOUR_OF_DAY);
            units = hour % 10;//个位
            tens = hour / 10;//十位
            bitmap = mBitmapManager.get(mBitmapManager.getMiddleNumberResId(tens), color);
            left = mPointScreenCenter.x - mRadiusInner - DimenUtil.dip2px(mContext, 4);
            top = mPointScreenCenter.y - bitmap.getHeight() - mLittleTriangleHeight;
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(mBitmapManager.getMiddleNumberResId(units), color);
            canvas.drawBitmap(bitmap, left, top, null);

            int marginBottom = DimenUtil.dip2px(mContext, 2);
            float lineLeft = mPointScreenCenter.x - mRadiusInner + DimenUtil.dip2px(mContext, 10);
            int lineHeight = DimenUtil.dip2px(mContext, 4);
            float lineTop = top - (marginBottom + lineHeight);
            float lineLen = 1.0f * bitmap.getWidth() / 4;

            //黄线上方的日期
            color = android.R.color.white;
            int month = instance.get(Calendar.MONTH) + 1;
            units = month % 10;//个位
            tens = month / 10;//十位
            left = (lineLeft + DimenUtil.dip2px(mContext, 10));
            bitmap = mBitmapManager.get(mBitmapManager.getSmallerNumberResId(tens));
            top -= (bitmap.getHeight() + marginBottom + 2.6f * lineHeight);
            canvas.drawBitmap(bitmap, left, top, null);
            lineLen += bitmap.getWidth();
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(mBitmapManager.getSmallerNumberResId(units), color);
            canvas.drawBitmap(bitmap, left, top, null);
            lineLen += bitmap.getWidth();

            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(R.drawable.my_health_diagonal, color);
            canvas.drawBitmap(bitmap, left, top, null);
            lineLen += bitmap.getWidth();

            int day = instance.get(Calendar.DAY_OF_MONTH);
            units = day % 10;//个位
            tens = day / 10;//十位
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(mBitmapManager.getSmallerNumberResId(tens), color);
            canvas.drawBitmap(bitmap, left, top, null);
            lineLen += bitmap.getWidth();
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(mBitmapManager.getSmallerNumberResId(units), color);
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
            int color = R.color.alphabet_uppercase;
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
                    bitmap = mBitmapManager.get(R.drawable.paint_number_2_smallest, color);
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
                    bitmap = mBitmapManager.get(R.drawable.paint_number_1_smallest, color);
                } else if (index == 16) {
                    bitmap = mBitmapManager.get(R.drawable.paint_number_0_smallest, color);
                } else if (index == 17) {
                    bitmap = mBitmapManager.get(R.drawable.paint_number_8_smallest, color);
                } else if (index == 19) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_b, color);
                } else if (index == 20) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_p, color);
                } else if (index == 21) {
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_m, color);
                } else if (index == 27) {//跑步
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_number_2, color);
                } else if (index == 28) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_number_1, color);
                } else if (index == 29) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_my_health_diagonal, color);
                } else if (index == 30) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_number_8, color);
                } else if (index == 31) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_number_0, color);
                } else if (index == 33) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_workout, color);
                } else if (index == 35) {//sport
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_t, color);
                } else if (index == 36) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_r, color);
                } else if (index == 37) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_o, color);
                } else if (index == 38) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_p, color);
                } else if (index == 39) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_s, color);
                } else if (index == 44) {//步数
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_number_0, color);
                } else if (index == 45) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_number_4, color);
                } else if (index == 46) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_number_9, color);
                } else if (index == 47) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_number_1, color);
                } else if (index == 48) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_number_4, color);
                } else if (index == 49) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_number_7, color);
                } else if (index == 51) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_steps, color);
                } else if (index == 53) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_s, color);
                } else if (index == 54) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_p, color);
                } else if (index == 55) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_e, color);
                } else if (index == 56) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_t, color);
                } else if (index == 57) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_s, color);
                } else if (index == 61) {//电量
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_sign_percentage, color);
                } else if (index == 62) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_number_0, color);
                } else if (index == 63) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_number_0, color);
                } else if (index == 64) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_number_1, color);
                } else if (index == 66) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_paint_battary, color);
                } else if (index == 68) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_y, color);
                } else if (index == 69) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_g, color);
                } else if (index == 70) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_r, color);
                } else if (index == 71) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_e, color);
                } else if (index == 72) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_n, color);
                } else if (index == 73) {
                    bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_e, color);
                } else if (index == 95) {//卡路里前半部分
                    bitmap = mBitmapManager.get(R.drawable.paint_calorie, color);
                    //bitmap = mBitmapManager.get(R.drawable.paint_calorie_bak, color);
                    //bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.paint_calorie_bak);
                } else if (index == 97) {
                    bitmap = mBitmapManager.get(R.drawable.paint_number_0_smallest, color);
                } else if (index == 98) {
                    bitmap = mBitmapManager.get(R.drawable.paint_number_5_smallest, color);
                } else if (index == 99) {
                    bitmap = mBitmapManager.get(R.drawable.paint_number_2_smallest, color);
                } else if (index == 0) {
                    bitmap = mBitmapManager.get(R.drawable.paint_number_9_smallest, color);//15926371198
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

}
