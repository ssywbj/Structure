package com.suheng.structure.view.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.suheng.structure.view.R;

public class AnimImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim_image_view);

        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_empty};
        states[1] = new int[]{};
        int[] colors = new int[]{Color.RED, Color.BLACK};
        ColorStateList colorStateList = new ColorStateList(states, colors);

        ImageView ivSelected = findViewById(R.id.aiv4_selected);
        ivSelected.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vector_delete));
        ivSelected.setImageTintList(colorStateList);
        ivSelected.setSelected(true);

        ImageView ivUnselected = findViewById(R.id.aiv4_unselected);
        ivUnselected.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vector_delete));
        ivUnselected.setImageTintList(colorStateList);
        ivUnselected.setSelected(false);

        findViewById(R.id.btn_selected).setOnClickListener(v -> {
            ivSelected.setSelected(true);
            ivUnselected.setSelected(false);
        });

        findViewById(R.id.btn_unselected).setOnClickListener(v -> {
            ivSelected.setSelected(false);
            ivUnselected.setSelected(true);
        });
    }

}
