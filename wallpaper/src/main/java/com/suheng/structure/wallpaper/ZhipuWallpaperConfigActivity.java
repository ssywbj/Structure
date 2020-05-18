package com.suheng.structure.wallpaper;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import androidx.annotation.Nullable;

@SuppressLint("ExportedPreferenceActivity")
public class ZhipuWallpaperConfigActivity extends PreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.wallpaper_zhipu_wallpaper_config_aty);
    }

}
