package com.suheng.structure.wallpaper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ZhipuWallpaperConfigActivity extends AppCompatActivity {

    public static final String PREFS_FILE = "zhipu_wallpaper_config";
    public static final String PREFS_KEY_BG_COLOR = "prefs_key_bg_color";
    private static final String PREFS_KEY_RB_CHECKED = "prefs_key_rb_checked";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallpaper_activity_zhipu_wallpaper_config);

        final SharedPreferences prefs = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        RadioGroup radioGroupBg = findViewById(R.id.radio_group_bg);
        ((RadioButton) radioGroupBg.findViewById(prefs.getInt(PREFS_KEY_RB_CHECKED, R.id.radio_btn_bg_blue))).setChecked(true);
        radioGroupBg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor edit = prefs.edit();
                if (checkedId == R.id.radio_btn_bg_blue) {
                    edit.putInt(PREFS_KEY_BG_COLOR, R.color.zhipu_watchface_interactive_mode);
                } else if (checkedId == R.id.radio_btn_bg_green) {
                    edit.putInt(PREFS_KEY_BG_COLOR, R.color.zhipu_watchface_interactive_mode_green);
                }
                edit.putInt(PREFS_KEY_RB_CHECKED, checkedId);
                edit.apply();
            }
        });
    }

}
