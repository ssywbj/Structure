package com.suheng.structure.view.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.suheng.structure.view.AnimImageView4;
import com.suheng.structure.view.AnimImageView5;
import com.suheng.structure.view.AnimImageView6;
import com.suheng.structure.view.EaseCubicInterpolator;
import com.suheng.structure.view.R;

public class AnimImageViewActivity extends AppCompatActivity {
    private static final int FIRST_PHASE_ANIM_DURATION = 1300;
    private static final int COMPLETE_ANIM_DURATION = 1700;
    private final EaseCubicInterpolator mFirstPhaseInterpolator = new EaseCubicInterpolator(0.33f, 0, 0.66f, 1);

    private AnimImageView4 mSelectedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim_image_view);

        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};
        int[] colors = new int[]{Color.argb((int) (255 * 0.8),0xFF,0,0), Color.BLACK};
        ColorStateList colorStateList = new ColorStateList(states, colors);

        AnimImageView4 ivSelected = findViewById(R.id.aiv4_selected);
        ivSelected.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vector_delete));
        ivSelected.setImageTintList(colorStateList);
        ivSelected.setSelected(true);
        mSelectedImageView = ivSelected;

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

                if (mSelectedImageView != null) {
                    if (mSelectedImageView.isSelectedAnimRunning()) {
                        return;
                    }
                    if (mSelectedImageView != animImageView4) {
                        mSelectedImageView.setSelectedAnim(false);
                    }
                }
                animImageView4.setSelectedAnim(true);
                mSelectedImageView = animImageView4;
            }
        };
        ivSelected.setOnClickListener(onClickListener);
        ivUnselected.setOnClickListener(onClickListener);

        this.initAnim5Layout(colorStateList);
        this.initAnim6Layout(colorStateList);
    }

    private void initAnim5Layout(ColorStateList colorStateList) {
        int defSelectedIndex = 0;
        ViewGroup aiv5Layout = findViewById(R.id.aiv5_layout);
        int childCount = aiv5Layout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = aiv5Layout.getChildAt(i);
            if (!(view instanceof AnimImageView5)) {
                continue;
            }

            AnimImageView5 imageView5 = (AnimImageView5) view;
            imageView5.setImageTintList(colorStateList);
            if (defSelectedIndex == 0) {
                view.setSelected(true);
                defSelectedIndex = 1;
            }

            imageView5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if (imageView5.isSelected() || imageView5.isSelectedAnimRunning()) {
                        return;
                    }*/

                    for (int i = 0; i < childCount; i++) {
                        View view = aiv5Layout.getChildAt(i);
                        if (!(view instanceof AnimImageView5)) {
                            continue;
                        }

                        AnimImageView5 viewTmp = (AnimImageView5) view;
                        if (viewTmp.isSelectedAnimRunning()) {
                            viewTmp.cancelSelectedAnimRunning();
                        } else {
                            if (viewTmp.isSelected()) {
                                viewTmp.setSelectedAnim(false);
                            }
                        }
                    }

                    imageView5.setSelectedAnim(true);
                }
            });

        }
    }

    private void initAnim6Layout(ColorStateList colorStateList) {
        int defSelectedIndex = 0;
        ViewGroup aiv5Layout = findViewById(R.id.aiv6_layout);
        int childCount = aiv5Layout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = aiv5Layout.getChildAt(i);
            if (!(view instanceof AnimImageView6)) {
                continue;
            }

            AnimImageView6 imageView6 = (AnimImageView6) view;
            //imageView6.setImageTintList(colorStateList);
            if (defSelectedIndex == 0) {
                view.setSelected(true);
                defSelectedIndex = 1;
            }

            imageView6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if (imageView6.isSelected() || imageView6.isSelectedAnimRunning()) {
                        return;
                    }*/

                    for (int i = 0; i < childCount; i++) {
                        View view = aiv5Layout.getChildAt(i);
                        if (!(view instanceof AnimImageView6)) {
                            continue;
                        }

                        AnimImageView6 viewTmp = (AnimImageView6) view;
                        if (viewTmp.isSelectedAnimRunning()) {
                            viewTmp.cancelSelectedAnimRunning();
                        } else {
                            if (viewTmp.isSelected()) {
                                viewTmp.setSelectedAnim(false);
                            }
                        }
                    }

                    imageView6.setSelectedAnim(true);
                }
            });

        }
    }

}
