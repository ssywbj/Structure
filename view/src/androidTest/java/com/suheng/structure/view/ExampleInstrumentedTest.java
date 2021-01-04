package com.suheng.structure.view;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.suheng.structure.view", appContext.getPackageName());
    }

    private Drawable mDrawable;
    private float mScale, mDegrees;

    @Before
    public void drawableInit() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mDrawable = ContextCompat.getDrawable(appContext, R.drawable.number_5_big);
        mScale = 2;
        mDegrees = 90;
    }

    @Test
    public void drawableToBitmap() {
        long currentTimeMillis = System.currentTimeMillis();
        BitmapHelper.drawableToBitmap(mDrawable, mScale, mDegrees);
        System.out.println("1, take time: " + (System.currentTimeMillis() - currentTimeMillis));
    }

    @Test
    public void drawableToBitmap2() {
        long currentTimeMillis = System.currentTimeMillis();
        BitmapHelper.drawableToBitmap2(mDrawable, mScale, mDegrees);
        System.out.println("2, take time: " + (System.currentTimeMillis() - currentTimeMillis));
    }

}
