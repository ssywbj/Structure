package com.suheng.structure.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.suheng.structure.view.R;
import com.suheng.structure.view.utils.RealBlur;

import java.util.ArrayList;
import java.util.List;

public class BlurActivity extends AppCompatActivity {
    private RealBlur mRealBlur;
    private final SuhengRecyclerFragment2 mFobRecyclerFrg2 = new SuhengRecyclerFragment2();
    private final SuhengRecyclerFragment3 mFobRecyclerFrg3 = new SuhengRecyclerFragment3();
    private final SuhengScrollFragment mSuhengScrollFragment = new SuhengScrollFragment();
    private SuhengBaseFragment mFrgCurrent;
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
        Log.d("Wbj", "getStatusBarHeight: " + getStatusBarHeight(this));
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            Log.d("Wbj", "getActionBarHeight: " + actionBar.getHeight());
        }*/
        mRealBlur = new RealBlur(this);

        mViewBlur = findViewById(R.id.foot_bar_root);
        ViewGroup barTabLayout = findViewById(R.id.foot_bar_tab_layout);
        ViewPager2 viewPager = findViewById(R.id.fragment_container);

        List<SuhengBaseFragment> frgs = new ArrayList<>();
        //frgs.add(new FobRecyclerFrg());
        frgs.add(mFobRecyclerFrg2);
        frgs.add(mFobRecyclerFrg3);
        frgs.add(mSuhengScrollFragment);
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
                Log.d("Wbj", "onPageSelected, position: " + position);
                super.onPageSelected(position);
                int childCount = barTabLayout.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = barTabLayout.getChildAt(i);
                    child.setSelected(i == position);
                }

                //mFootBar.setItemSelectState(position);
                int item = position % frgs.size();
                viewPager.setCurrentItem(item);
                SuhengBaseFragment suhengBaseFragment = frgs.get(item);
                //mFootBar.toggleDynamicBlur(fobBaseFrg.getBlurredView());
            }
        });
        viewPager.setCurrentItem(0);
        mFrgCurrent = mFobRecyclerFrg2;

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

        this.setViewBlurred(mFrgCurrent.getBlurredView());
    }

    public void setViewBlurred(View viewBlurred) {
        mRealBlur.setViewBlurredAndBlur(viewBlurred, mViewBlur);
    }

    public View getViewBlur() {
        return mViewBlur;
    }

    public RealBlur getDynamicBlur() {
        return mRealBlur;
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
            return !menu && !back;
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