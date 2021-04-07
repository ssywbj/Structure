package com.suheng.structure.hook;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("HookActivity");
        textView.setTextSize(20);
        textView.setTextColor(Color.RED);
        setContentView(textView);
    }

}
