package com.suheng.structure.view.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.suheng.structure.view.AnimImageView4;
import com.suheng.structure.view.R;

public class AnimImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim_image_view);

        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};
        int[] colors = new int[]{Color.RED, Color.BLACK};
        ColorStateList colorStateList = new ColorStateList(states, colors);

        AnimImageView4 ivSelected = findViewById(R.id.aiv4_selected);
        ivSelected.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vector_delete));
        ivSelected.setImageTintList(colorStateList);
        ivSelected.setSelected(true);
        //mSelectedImageView = ivSelected;

        AnimImageView4 ivUnselected = findViewById(R.id.aiv4_unselected);
        ivUnselected.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vector_delete));
        ivUnselected.setImageTintList(colorStateList);
        ivUnselected.setSelected(false);

        /*findViewById(R.id.btn_selected).setOnClickListener(v -> {
            ivSelected.setSelected(true);
            ivUnselected.setSelected(false);
        });

        findViewById(R.id.btn_unselected).setOnClickListener(v -> {
            ivSelected.setSelected(false);
            ivUnselected.setSelected(true);
        });*/

        /*ivSelected.setOnClickListener(v -> {
            if (AnimImageView4.sSelectedView != null && AnimImageView4.sSelectedView.isSelectedAnimRunning()) {
                return;
            }

            if (AnimImageView4.sSelectedView != ivSelected) {
                if (AnimImageView4.sSelectedView != null) {
                    AnimImageView4.sSelectedView.setSelectedAnim(false);
                }
            }
            ivSelected.setSelectedAnim(true);
        });
        ivUnselected.setOnClickListener(v -> {
            if (AnimImageView4.sSelectedView != null && AnimImageView4.sSelectedView.isSelectedAnimRunning()) {
                return;
            }

            if (AnimImageView4.sSelectedView != ivUnselected) {
                if (AnimImageView4.sSelectedView != null) {
                    AnimImageView4.sSelectedView.setSelectedAnim(false);
                }
            }
            ivUnselected.setSelectedAnim(true);
        });*/

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimImageView4 animImageView4 = null;
                if (v instanceof AnimImageView4) {
                    animImageView4 = (AnimImageView4) v;
                }
                if (animImageView4 == null) {
                    return;
                }

                if (AnimImageView4.sSelectedView != null && AnimImageView4.sSelectedView.isSelectedAnimRunning()) {
                    return;
                }
                if (AnimImageView4.sSelectedView != animImageView4) {
                    if (AnimImageView4.sSelectedView != null) {
                        AnimImageView4.sSelectedView.setSelectedAnim(false);
                    }
                }
                animImageView4.setSelectedAnim(true);
            }
        };
        ivSelected.setOnClickListener(onClickListener);
        ivUnselected.setOnClickListener(onClickListener);
    }

}
