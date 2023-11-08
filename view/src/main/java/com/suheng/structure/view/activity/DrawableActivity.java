package com.suheng.structure.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOverlay;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.suheng.structure.view.R;
import com.suheng.structure.view.drawable.ProgressDrawable;

public class DrawableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable);
        ImageView imageView = findViewById(R.id.image_drawable);
        Drawable one = AppCompatResources.getDrawable(this, R.drawable.icon_calendar);
        Drawable two = AppCompatResources.getDrawable(this, R.drawable.icon_folder);

        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{
                ContextCompat.getDrawable(this, R.drawable.beauty), ContextCompat.getDrawable(this, R.drawable.beauty2)});
        //imageView.setImageDrawable(transitionDrawable);

        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{one, two});
        imageView.setImageDrawable(layerDrawable);
        MaskCircleDrawable maskCircleDrawable = new MaskCircleDrawable(new Drawable[]{one, two});

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionDrawable.startTransition(2000);

                /*Drawable byLayerId = layerDrawable.findDrawableByLayerId(0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int indexByLayerId = layerDrawable.findIndexByLayerId(0);
                }
                Drawable drawable = layerDrawable.getDrawable(0);
                imageView.setImageDrawable(drawable);*/

                Drawable drawable = maskCircleDrawable.getDrawable(0);
                maskCircleDrawable.startTransition();
                imageView.setImageDrawable(drawable);
            }
        });

        findViewById(R.id.imageView).setOnClickListener(view -> {
            ViewOverlay overlay = view.getOverlay();
            ProgressDrawable drawable = new ProgressDrawable(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    overlay.clear();
                }
            });
            drawable.setBounds(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            overlay.add(drawable);
        });
    }

}
