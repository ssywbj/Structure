package com.wiz.watch.facephoto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.utils.DimenUtil;
import com.structure.wallpaper.basic.view.FaceAnimView;

import java.io.File;
import java.util.Calendar;

public class PhotoWatchFace extends FaceAnimView {
    private PhotoBitmapManager mBitmapManager;
    private SharedPreferences mPrefs;
    private Bitmap mBitmap;
    private final RectF mRectF = new RectF();
    private float mScaleTime, mScaleDate;
    private int mPictureWidth, mPointHalfWidth;
    private boolean mIsRoundScreen;

    public PhotoWatchFace(Context context, boolean isDimMode) {
        super(context, false, false, isDimMode);
        this.init();
    }

    public PhotoWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void init() {
        setDefaultTime(8, 36, TIME_NONE);
        needAppearAnimNumber();
        mBitmapManager = new PhotoBitmapManager(mContext);
        mBitmapManager.setRoundScreen(mIsRoundScreen = isScreenRound());

        mPrefs = mContext.getSharedPreferences(PhotoFaceConfigFragment.PREFS_FILE, Context.MODE_PRIVATE);
        this.getPhoto();

        mScaleTime = Float.parseFloat(mContext.getString(R.string.ratio));
        mScaleDate = mScaleTime / 2.15f;

        mPointHalfWidth = (int) (mBitmapManager.getPointBitmap(Color.WHITE, mScaleDate).getWidth() / 2f);
        final int timeWidth = mBitmapManager.getNumberBitmap(0, Color.WHITE, mScaleTime).getWidth() * 2;
        final int dateWidth = mBitmapManager.getNumberBitmap(0, Color.WHITE, mScaleDate).getWidth() * 4 + mPointHalfWidth;
        mPictureWidth = Math.max(timeWidth, dateWidth);
    }

    private void getPhoto() {
        String path = mPrefs.getString(PhotoFaceConfigFragment.PREFS_KEY_PATH, "");
        if (TextUtils.isEmpty(path)) {
            mBitmap = this.getDefaultPhoto();
        } else {
            File file = new File(path);
            if (file.exists() && file.canRead()) {
                mBitmap = BitmapFactory.decodeFile(path);
            } else {
                mBitmap = this.getDefaultPhoto();
            }
        }
    }

    private Bitmap getDefaultPhoto() {
        if (mIsRoundScreen) {
            return BitmapFactory.decodeResource(getResources(), R.drawable.round_photo_face_default);
        } else {
            return BitmapFactory.decodeResource(getResources(), R.drawable.photo_face_default);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF.set(0, 0, w, h);

        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PhotoFaceConfigFragment.ACTION_UPDATE_FACE);
        mContext.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (!visible) {
            unregisterTimeTickReceiver();
        }
    }

    @Override
    protected void onAppearAnimFinished() {
        registerTimeTickReceiver();
        updateTime();
        invalidate();
    }

    public void destroy() {
        mBitmapManager.clear();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.transparent), PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(mBitmap, null, mRectF, null);

        this.paintDate(canvas, mHour, mMinute);
    }

    private final Picture mPicture = new Picture();

    private void paintDate(Canvas canvas, int hour, int minute) {
        Canvas cns = mPicture.beginRecording(mPictureWidth, 0);
        int color = ContextCompat.getColor(mContext, android.R.color.white);
        Calendar instance = Calendar.getInstance();
        //时
        cns.translate(mPictureWidth / 2f, 0);
        int tens = hour / 10;//十位
        int units = hour % 10;//个位
        Bitmap bitmap = mBitmapManager.getNumberBitmap(tens, color, mScaleTime);
        cns.drawBitmap(bitmap, -bitmap.getWidth(), 0, null);
        bitmap = mBitmapManager.getNumberBitmap(units, color, mScaleTime);
        cns.drawBitmap(bitmap, 0, 0, null);
        float pictureHeight = (bitmap.getHeight() + DimenUtil.dip2px(mContext, 10));
        //分
        tens = minute / 10;//十位
        units = minute % 10;//个位
        bitmap = mBitmapManager.getNumberBitmap(tens, color, mScaleTime);
        cns.drawBitmap(bitmap, -bitmap.getWidth(), pictureHeight, null);
        bitmap = mBitmapManager.getNumberBitmap(units, color, mScaleTime);
        cns.drawBitmap(bitmap, 0, pictureHeight, null);
        pictureHeight += (bitmap.getHeight() + DimenUtil.dip2px(mContext, 14));
        //点
        bitmap = mBitmapManager.getPointBitmap(color, mScaleDate);
        float left = -mPointHalfWidth;
        cns.drawBitmap(bitmap, left, pictureHeight, null);
        left += (bitmap.getWidth() - mPointHalfWidth / 2f);
        //日
        int day = instance.get(Calendar.DAY_OF_MONTH);
        tens = day / 10;//十位
        units = day % 10;//个位
        bitmap = mBitmapManager.getNumberBitmap(tens, color, mScaleDate);
        cns.drawBitmap(bitmap, left, pictureHeight, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.getNumberBitmap(units, color, mScaleDate);
        cns.drawBitmap(bitmap, left, pictureHeight, null);
        //月
        left = -mPointHalfWidth + mPointHalfWidth / 2f;
        int month = instance.get(Calendar.MONTH) + 1;
        tens = month / 10;//十位
        units = month % 10;//个位
        bitmap = mBitmapManager.getNumberBitmap(units, color, mScaleDate);
        left -= bitmap.getWidth();
        cns.drawBitmap(bitmap, left, pictureHeight, null);
        bitmap = mBitmapManager.getNumberBitmap(tens, color, mScaleDate);
        left -= bitmap.getWidth();
        cns.drawBitmap(bitmap, left, pictureHeight, null);
        pictureHeight += bitmap.getHeight();
        mPicture.endRecording();

        float x, y;
        if (mIsRoundScreen) {
            x = getWidth() / 2f - mPictureWidth / 2f;
            y = getHeight() / 2f - pictureHeight / 2f;
        } else {
            x = mContext.getResources().getDimension(R.dimen.margin_left);
            y = x + mContext.getResources().getDimension(R.dimen.margin_top);
        }
        canvas.translate(x, y);
        canvas.drawPicture(mPicture);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (PhotoFaceConfigFragment.ACTION_UPDATE_FACE.equals(intent.getAction())) {
                getPhoto();
                invalidate();
            }
        }
    };

}
