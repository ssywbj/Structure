package com.wiz.watch.facepeach.interfaces;

import android.graphics.Bitmap;

public interface IPunctuationBitmapManager {
    Bitmap getPunctuation(char sign, int type, int color);
}
