package com.wiz.watch.facepeach;

import android.content.Context;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

public class PeachWatchFace{
    public static final String CACHE_KEY = "PeachWatchFace";

    private Context context;

    private View root;

    private LottieAnimationView lottieAnimationView;

    private TimeView timeView;

    private MonitorFrameLayout monitor;

    public PeachWatchFace(View root) {
        this.root = root;
        this.context = root.getContext();
        lottieAnimationView = root.findViewById(R.id.animation_view);
        timeView = root.findViewById(R.id.time);
        monitor = root.findViewById(R.id.monitor);

        lottieAnimationView.setImageResource(R.drawable.bg);
        lottieAnimationView.setAnimation(context.getResources().openRawResource(R.raw.peach),CACHE_KEY);
        lottieAnimationView.setRepeatCount(0);


        lottieAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottieAnimationView.playAnimation();
            }
        });

    }

    public void destroy() {
        if (lottieAnimationView != null && timeView != null) {
            lottieAnimationView.cancelAnimation();
            timeView.stopListenTime();
        }
    }

}
