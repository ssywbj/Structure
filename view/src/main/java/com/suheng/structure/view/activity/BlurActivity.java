package com.suheng.structure.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.renderscript.Toolkit;
import com.suheng.structure.view.R;

import java.util.ArrayList;
import java.util.List;

public class BlurActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur);

        /*DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Log.d("Wbj", "getDisplayMetrics: " + displayMetrics.widthPixels + ", " + displayMetrics.heightPixels);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        Log.d("Wbj", "getRealMetrics: " + metrics.widthPixels + ", " + metrics.heightPixels);
        Log.d("Wbj", "getStatusBarHeight: " + getStatusBarHeight(this));
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            Log.d("Wbj", "getActionBarHeight: " + actionBar.getHeight());
        }*/

        View view = findViewById(R.id.blur_foot_bar);

        List<FobBaseFrg> frgs = new ArrayList<>();
        frgs.add(new FobRecyclerFrg());
        frgs.add(new FobScrollFrg());
        ViewPager viewPager = findViewById(R.id.fragment_container);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return frgs.get(position);
            }

            @Override
            public int getCount() {
                return frgs.size();
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //mFootBar.setItemSelectState(position);
                int item = position % frgs.size();
                viewPager.setCurrentItem(item);
                FobBaseFrg fobBaseFrg = frgs.get(item);
                //mFootBar.toggleDynamicBlur(fobBaseFrg.getBlurredView());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        int radius = 15;
        Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.girl_gaitubao);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.MIRROR));
            view.setBackground(new BitmapDrawable(getResources(), srcBitmap));
        } else {
            Bitmap blurBitmap = Toolkit.INSTANCE.blur(srcBitmap, radius);
            view.setBackground(new BitmapDrawable(getResources(), blurBitmap));
        }

        view.post(new Runnable() {
            @Override
            public void run() {
                getViewLocation(view);
            }
        });

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
     * 虚拟操作拦（home等）是否显示
     */
    public static boolean isNavigationBarShow(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 获取虚拟操作拦（home等）高度
     */
    public static int getNavigationBarHeight(Activity activity) {
        if (!isNavigationBarShow(activity)) {
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

}