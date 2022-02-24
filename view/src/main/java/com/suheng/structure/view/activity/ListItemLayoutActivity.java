package com.suheng.structure.view.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.suheng.structure.view.ListItemLayout;
import com.suheng.structure.view.R;

import java.util.Random;

public class ListItemLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_layout);

        ListItemLayout listItemLayout5 = findViewById(R.id.list_item_layout5);
        findViewById(R.id.list_item_layout4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int nextInt = random.nextInt(4);
                if (nextInt == 0) {
                    listItemLayout5.resetCornersRadius();
                } else if (nextInt == 1) {
                    listItemLayout5.cornersRound();
                } else if (nextInt == 2) {
                    listItemLayout5.topCornersRound();
                } else {
                    listItemLayout5.bottomCornersRound();
                }
            }
        });
    }

}
