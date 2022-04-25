package com.suheng.structure.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.suheng.structure.view.ListItemLayout;
import com.suheng.structure.view.R;

public class ListItemLayoutActivity extends AppCompatActivity {
    private int mItemRadioHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_layout);

        ViewGroup itemLayout1 = findViewById(R.id.list_item_root);
        //ViewGroup itemLayout1 = findViewById(R.id.list_item_layout1);

        /*int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        itemLayout1.setLayoutAnimation(animation);*/

        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setInterpolator(LayoutTransition.APPEARING, new PathInterpolator(0.25f, 0, 0, 1));
        layoutTransition.setInterpolator(LayoutTransition.DISAPPEARING, new PathInterpolator(0.25f, 0, 0, 1));
        /*layoutTransition.setInterpolator(LayoutTransition.CHANGE_APPEARING, new EaseCubicInterpolator(0.25f, 0, 0, 1));
        layoutTransition.setInterpolator(LayoutTransition.DISAPPEARING, new EaseCubicInterpolator(0.25f, 0, 0, 1));*/

        layoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
            @Override
            public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                Log.d("Wbi", "startTransition, view: " + view + ", transitionType: " + transitionType + ", " + container.getChildCount()
                        + "====" + itemLayout1.getChildCount());
                if (transitionType == LayoutTransition.CHANGE_APPEARING) {
                    int childCount = itemLayout1.getChildCount();
                    int index = childCount - 1;
                    if (index < 0) {
                        return;
                    }

                    View child = itemLayout1.getChildAt(index);
                    if (child instanceof ListItemLayout) {
                        ListItemLayout listItemLayout = (ListItemLayout) child;
                        if (index == 0) {
                            listItemLayout.topCornersRound();
                        } else {
                            listItemLayout.cornersRight();
                        }
                    }
                }
            }

            @Override
            public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                Log.v("Wbi", "endTransition, view: " + view + ", transitionType: " + transitionType + ", " + container.getChildCount()
                        + "====" + itemLayout1.getChildCount());
                if (transitionType == LayoutTransition.DISAPPEARING) {
                    int childCount = itemLayout1.getChildCount();
                    int index = childCount - 1;
                    if (index < 0) {
                        return;
                    }

                    View child = itemLayout1.getChildAt(index);
                    if (child instanceof ListItemLayout) {
                        ListItemLayout listItemLayout = (ListItemLayout) child;
                        if (index == 0) {
                            listItemLayout.cornersRound();
                        } else {
                            listItemLayout.bottomCornersRound();
                        }
                    }
                }
            }
        });

        /*ObjectAnimator appearAnim = ObjectAnimator.ofFloat(null, "translationY", -100, 0);
        ObjectAnimator disappearAnim = ObjectAnimator.ofFloat(null, "translationY", 100, 0);
        layoutTransition.setAnimator(LayoutTransition.APPEARING, appearAnim);
        layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, disappearAnim);*/

        layoutTransition.setDuration(3000);
        itemLayout1.setLayoutTransition(layoutTransition);
        //itemLayout1.setLayoutTransition(layoutTransition);
        //itemLayout1.setLayoutAnimation();
        /*itemLayout1.setLayoutAnimation();*/

        ListItemLayout itemSwitch = findViewById(R.id.item_switch);
        ListItemLayout itemRadio = findViewById(R.id.item_radio);
        itemRadio.post(new Runnable() {
            @Override
            public void run() {
                mItemRadioHeight = itemRadio.getMeasuredHeight();
            }
        });
        findViewById(R.id.list_item_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LayoutInflater.from(ListItemLayoutActivity.this).inflate(R.layout.view_list_item_switch, itemLayout1, true);
                //findViewById(R.id.list_item_5).setVisibility(View.VISIBLE);
                //findViewById(R.id.list_item_6).setVisibility(View.VISIBLE);

                ValueAnimator animator = ValueAnimator.ofInt(mItemRadioHeight, 0/*(int) itemRadio.mRadius*/);
                //ValueAnimator animator = ValueAnimator.ofInt(measuredHeight, (int) itemRadio.mRadius);
                animator.setDuration(300);
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Object value = animation.getAnimatedValue();
                        itemRadio.setSettingFixedHeight((Integer) value);
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        itemSwitch.cornersRoundWithAnim();
                    }
                });
                animator.start();
            }
        });

        findViewById(R.id.list_item_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int childCount = itemLayout1.getChildCount();
                //itemLayout1.removeViewAt(childCount - 1);
                //findViewById(R.id.list_item_5).setVisibility(View.GONE);
                //findViewById(R.id.list_item_6).setVisibility(View.GONE);
                //itemLayout1.getChildAt(childCount - 1).setVisibility(View.GONE);

                ValueAnimator animator = ValueAnimator.ofInt(0, mItemRadioHeight);
                animator.setDuration(300);
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Object value = animation.getAnimatedValue();
                        itemRadio.setSettingFixedHeight((Integer) value);
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationEnd(animation);
                        //itemSwitch.topCornersRoundWithAnim();
                        itemSwitch.topCornersRound();
                    }
                });
                animator.start();
            }
        });

        ListItemLayout listItemLayout = findViewById(R.id.list_item_5);
        ImageView viewLeftImage = listItemLayout.getViewLeftImage();
        if (viewLeftImage != null) {
            this.loadImage(viewLeftImage);
        }
    }

    private void loadImage(ImageView imageView) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.beauty2, options);
        float density = getResources().getDisplayMetrics().density;
        int size = (int) (38 * density);
        int widthSampleSize = options.outWidth / size;
        int heiSampleSize = options.outHeight / size;
        Log.d("loadImage", "size: " + size + ", outWidth: " + options.outWidth + ", outHeight: " + options.outHeight
                + ", inSampleSize: " + options.inSampleSize + ", inDensity: " + options.inDensity + ", density: " + density);
        options.inSampleSize = Math.min(widthSampleSize, heiSampleSize);
        options.inSampleSize = Math.max(options.inSampleSize, 1);
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.beauty2, options);
        Log.i("loadImage", "outWidth: " + options.outWidth + ", outHeight: " + options.outHeight + ", inSampleSize: "
                + options.inSampleSize + "===getWidth: " + bitmap.getWidth() + ", getHeight: " + bitmap.getHeight());
        Bitmap target = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(target);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        RectF rectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        int radius = 30;
        canvas.drawRoundRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), radius, radius, paint);
        canvas.save();
        Bitmap bitmapOver = BitmapFactory.decodeResource(getResources(), R.drawable.earth);
        Log.v("loadImage", "getWidth: " + bitmapOver.getWidth() + ", getHeight: " + bitmapOver.getHeight());
        canvas.translate(rectF.centerX(), rectF.centerY());
        canvas.drawBitmap(bitmapOver, -bitmapOver.getWidth() / 2f, -bitmapOver.getHeight() / 2f, null);
        canvas.restore();

        imageView.setImageBitmap(target);

        bitmap.recycle();
    }
}
