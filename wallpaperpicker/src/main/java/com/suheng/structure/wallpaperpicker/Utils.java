package com.suheng.structure.wallpaperpicker;

import java.util.Locale;

public class Utils {

    public static String formatDuration(long millis) {
        // 将毫秒转换为总秒数
        final long totalSeconds = millis / 1000;

        // 计算分钟和秒
        final long minutes = totalSeconds / 60;
        final long seconds = totalSeconds % 60;

        // 格式化为 "MM:SS" 的形式
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
