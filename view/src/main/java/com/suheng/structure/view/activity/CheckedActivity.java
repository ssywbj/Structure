package com.suheng.structure.view.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.suheng.structure.view.AnimCheckBox;
import com.suheng.structure.view.R;

public class CheckedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked);

        AnimCheckBox animCB = findViewById(R.id.anim_check_box);
        //animCB.setChecked(false);
        ImageView imageCheckedDrawable = findViewById(R.id.image_checked_drawable);
        //imageCheckedDrawable.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.checkbox_checked));
        //imageCheckedDrawable.setImageDrawable(new CheckedDrawable(Color.RED));

        RadioGroup radioGroup = findViewById(R.id.svg_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //Log.i(AnimRadioButton.TAG, "RadioGroup, checkedId: " + checkedId);
            }
        });
        animCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroup.clearCheck();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        menu.findItem(R.id.menu_copy).setCheckable(true).setChecked(true);
        menu.findItem(R.id.menu_setting).setCheckable(true).setChecked(false);
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Toast.makeText(this, "item = " + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
        //return true;
    }
}
