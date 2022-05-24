package com.suheng.structure.view.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.suheng.structure.view.R;

public class SVGActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svg);


        /*AnimationDrawable drawable = (AnimationDrawable) ContextCompat.getDrawable(this, R.drawable.map_my_location_img);
        //drawable.start();
        ImageView imageView = findViewById(R.id.view_image);
        imageView.setImageDrawable(drawable);
        drawable.start();*/

        ImageView imageView = findViewById(R.id.image_svg);
        AnimatedVectorDrawableCompat vectorDrawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.water_drop_anim);
        vectorDrawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.tt_search_anim);
        if (vectorDrawableCompat != null) {
            imageView.setImageDrawable(vectorDrawableCompat);
            vectorDrawableCompat.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                @Override
                public void onAnimationStart(Drawable drawable) {
                    super.onAnimationStart(drawable);
                    Log.d("Wbj", "-----svg anim start-----");
                }

                @Override
                public void onAnimationEnd(Drawable drawable) {
                    super.onAnimationEnd(drawable);
                    Log.d("Wbj", "-----svg anim end-----");
                }
            });

            vectorDrawableCompat.start();
        }
    }

}
