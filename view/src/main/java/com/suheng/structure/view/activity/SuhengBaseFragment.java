package com.suheng.structure.view.activity;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class SuhengBaseFragment extends Fragment {

    @Nullable
    public abstract View getBlurredView();
}
