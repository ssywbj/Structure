package com.suheng.structure.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.renderscript.Toolkit;
import com.suheng.structure.view.R;
import com.suheng.structure.view.utils.RealBlur;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BlurActivity extends AppCompatActivity {
    private static final String TAG = BlurActivity.class.getSimpleName();
    private final SuhengRecyclerFragment2 mFobRecyclerFrg2 = new SuhengRecyclerFragment2();
    private final SuhengRecyclerFragment3 mFobRecyclerFrg3 = new SuhengRecyclerFragment3();
    private final SuhengScrollFragment mSuhengScrollFragment = new SuhengScrollFragment();
    private SuhengBaseFragment mFrgCurrent;
    private ImageView mTopViewBlur;
    private View mViewBlur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur);

        /*DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Log.d("Wbj", "getDisplayMetrics: " + displayMetrics.widthPixels + ", " + displayMetrics.heightPixels);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        Log.d("Wbj", "getRealMetrics: " + metrics.widthPixels + ", " + metrics.heightPixels);
        Log.d("Wbj", "getStatusBarHeight: " + getStatusBarHeight(this));*/

        mTopViewBlur = findViewById(R.id.frg_fob_image_blur);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mViewBlur = findViewById(R.id.foot_bar_blur12);
        } else {
            mViewBlur = findViewById(R.id.foot_bar_root);
        }
        ViewGroup barTabLayout = findViewById(R.id.foot_bar_tab_layout);
        ViewPager2 viewPager = findViewById(R.id.fragment_container);

        List<SuhengBaseFragment> frgs = new ArrayList<>();
        frgs.add(mFobRecyclerFrg2);
        //frgs.add(mFobRecyclerFrg3);
        frgs.add(mSuhengScrollFragment);
        mFrgCurrent = frgs.get(0);

        RealBlur realBlur = new RealBlur();
        realBlur.setViewBlurredAndBlur(mFrgCurrent.getBlurredView(), mViewBlur);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return frgs.get(position);
            }

            @Override
            public int getItemCount() {
                return frgs.size();
            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int childCount = barTabLayout.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = barTabLayout.getChildAt(i);
                    child.setSelected(i == position);
                }

                int item = position % frgs.size();
                viewPager.setCurrentItem(item);
                mFrgCurrent = frgs.get(item);

                Log.d(TAG, "onPageSelected, position: " + position + ", isAdded: " + mFrgCurrent.isAdded() + ", blurredView: " + mFrgCurrent.getBlurredView());
                if (mFrgCurrent.isAdded()) {
                    realBlur.updateViewBlurred(mFrgCurrent.getBlurredView());
                } else {
                    mViewBlur.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.v(TAG, "postDelayed, onPageSelected, position: " + position + ", isAdded: " + mFrgCurrent.isAdded() + ", blurredView: " + mFrgCurrent.getBlurredView());
                            realBlur.updateViewBlurred(mFrgCurrent.getBlurredView());
                        }
                    }, 60);
                }

            }
        });
        viewPager.setCurrentItem(0);

        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};
        int[] colors = new int[]{Color.BLACK, Color.LTGRAY};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        int childCount = barTabLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = barTabLayout.getChildAt(i);
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                textView.setTextColor(colorStateList);
            }

            child.setSelected(i == 0);
            child.setTag(i);

            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object tag = child.getTag();
                    if (tag instanceof Integer) {
                        int index = (int) tag;
                        if (index < frgs.size()) {
                            viewPager.setCurrentItem(index);
                        }
                    }
                }
            });
        }

        //this.setViewContainerBg();

        mTopViewBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                //mViewBlur.getLocationOnScreen(location);
                mTopViewBlur.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];

                View blurredView = mFrgCurrent.getBlurredView();
                if (blurredView == null) {
                    return;
                }
                /*blurredView.getLocationOnScreen(location);
                int x1 = location[0];
                int y1 = location[1];

                int scaleFactor = 1;
                int width = mViewBlur.getWidth();
                int height = mViewBlur.getHeight();
                int bitmapWidth = (int) Math.ceil(1f * width / scaleFactor);
                int bitmapHeight = (int) Math.ceil(1f * height / scaleFactor);
                Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_4444);
                Canvas canvas = new Canvas(bitmap);
                float scale = 1f / scaleFactor;
                float dx = x1 - x;
                float dy = y1 - y;
                canvas.scale(scale, scale);
                canvas.translate(dx, dy*//*+blurredView.getScrollY()*//*);
                Log.d("Wbj", "onClick, x: " + x + ", y: " + y + ", x1: " + x1 + ", y1: " + y1 + ", scale: " + scale + ", dx: " + dx + ", dy: " + dy);
                Log.d("Wbj", "onClick, width: " + width + ", height: " + height + ", bitmapWidth: " + bitmapWidth + ", bitmapHeight: " + bitmapHeight
                        + ", blurredWidth: " + blurredView.getWidth() + "---" + blurredView.getMeasuredWidth() + ", blurredHeight: " + blurredView.getHeight() + "---" + blurredView.getMeasuredHeight());
                blurredView.draw(canvas);
                Bitmap bg = null;
                if (mTopViewBlur.getBackground() instanceof BitmapDrawable) {
                    bg = ((BitmapDrawable) mTopViewBlur.getBackground()).getBitmap();
                }
                mTopViewBlur.setBackground(new BitmapDrawable(getResources(), bitmap));
                if (bg != null && !bg.isRecycled()) {
                    bg.recycle();
                }*/

                //mTopViewBlur.getLocationOnScreen(location);
                x = location[0];
                y = location[1];
                Rect rect = new Rect();
                rect.set(x, y, x + mTopViewBlur.getWidth(), y + mTopViewBlur.getHeight());
                Bitmap bitmap = intersectsViewBitmap(blurredView, rect);
                //mTopViewBlur.setImageBitmap(bitmap);
                if (bitmap != null) {
                    mTopViewBlur.setBackground(new BitmapDrawable(getResources(), Toolkit.INSTANCE.blur(bitmap, 15)));
                }
            }
        });
    }

    private void setViewContainerBg() {
        View viewCover = findViewById(R.id.foot_bar_cover);
        float alpha = 0.9f;
        int color = Color.WHITE;
        int newColor = Color.argb((int) (255 * alpha), Color.red(color), Color.green(color), Color.blue(color));

        viewCover.setBackgroundColor(newColor);

        /*ShapeDrawable shapeDrawable = new ShapeDrawable();
        Paint paint = shapeDrawable.getPaint();
        viewCover.post(new Runnable() {
            @Override
            public void run() {
                paint.setShader(new LinearGradient(0, 0, 0, viewCover.getMeasuredHeight(), newColor, color, Shader.TileMode.CLAMP));
                viewCover.setBackground(shapeDrawable);
            }
        });*/

        boolean supportNavigationBar = isSupportNavigationBar(this);
        Log.d(TAG, "supportNavigationBar: " + supportNavigationBar);
        if (supportNavigationBar) {
            getWindow().setNavigationBarColor(color);
        }
    }

    public View getTopViewBlur() {
        return mTopViewBlur;
    }

    public View getViewBlur() {
        return mViewBlur;
    }

    public static int getStatusBarHeight(Context context) {
        int height = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = context.getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

    /**
     * 判断是否支持虚拟导航栏
     */
    public static boolean isSupportNavigationBar(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (wm == null) {
                return false;
            }

            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y || realSize.x != size.x;
        } else {
            boolean menu = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !menu && !back;
        }
    }

    /**
     * 获取虚拟操作拦（home等）高度
     */
    public static int getNavigationBarHeight(Activity activity) {
        if (!isSupportNavigationBar(activity)) {
            return 0;
        }

        int height = 0;
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public static void getViewLocation(View view) {
        Log.v("Wbj", "getViewLocation, getMeasuredWidth: " + view.getMeasuredWidth() + ", getMeasuredHeight:" + view.getMeasuredHeight());

        int[] location = new int[2];
        view.getLocationInWindow(location);
        int x = location[0];
        int y = location[1];
        Log.d("Wbj", "getLocationInWindow, x: " + x + ", y:" + y);
        int[] location1 = new int[2];
        view.getLocationOnScreen(location1);
        x = location1[0];
        y = location1[1];
        Log.d("Wbj", "getLocationOnScreen, x: " + x + ", y:" + y);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            int[] location2 = new int[2];
            view.getLocationInSurface(location2);
            x = location2[0];
            y = location2[1];
            Log.d("Wbj", "getLocationInSurface, x: " + x + ", y:" + y);
        }
    }

    /**
     * @param view 要截取的View
     * @param r    要截取的区域
     * @return bitmap: View的区域位图
     */
    public static Bitmap intersectsViewBitmap(View view, Rect r) {
        //获取View的位置及边界信息
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        Rect viewRect = new Rect();
        viewRect.set(x, y, x + view.getWidth(), y + view.getHeight());

        Rect rect = new Rect(r);
        Log.d(TAG, "before intersect rect: " + rect);
        boolean intersect = rect.intersect(viewRect);
        if (intersect) { //要截取的区域要在View的区域之中
            Log.i(TAG, "after intersect rect: " + rect);
            Bitmap bitmap;
            int dy = y - rect.top;
            int dx = x - rect.left;
            if ((view instanceof NestedScrollView) || (view instanceof ScrollView)) {
                int scrollRange = 0;
                try {
                    Method method = View.class.getDeclaredMethod("computeVerticalScrollRange");
                    method.setAccessible(true);
                    Object invoke = method.invoke(view);
                    if (invoke instanceof Integer) {
                        scrollRange = (int) invoke;
                        Log.i(TAG, "reflect invoke scrollRange: " + scrollRange);
                    }
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    Log.e(TAG, "reflect invoke computeVerticalScrollRange() fail!", e);
                }

                if (scrollRange == 0) {
                    return null;
                }

                int scrollY = view.getScrollY();
                Bitmap viewBitmap = Bitmap.createBitmap(viewRect.width() , scrollRange, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(viewBitmap);
                view.draw(canvas);
                Log.i(TAG, "canvas w h: " + canvas.getWidth() + ", " + canvas.getHeight());
                bitmap = Bitmap.createBitmap(viewBitmap, -dx, scrollY - dy , rect.width() , rect.height() ); //截取片断
                if (!viewBitmap.isRecycled()) {
                    viewBitmap.recycle();
                }
            } else {
                bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.translate(dx, dy); //平移到要截取的位置
                view.draw(canvas);
            }

            return bitmap;
        } else { //两个View没有相交部分
            Log.w(TAG, "Hasn't intersect region between two views!");
            return null;
        }
    }

}