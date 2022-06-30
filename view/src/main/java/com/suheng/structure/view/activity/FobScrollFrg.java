/*Transsion Top Secret*/
package com.suheng.structure.view.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.renderscript.Toolkit;
import com.suheng.structure.view.R;

public class FobScrollFrg extends FobBaseFrg {

    private View mBlurredView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fob_scroll, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBlurredView = view.findViewById(R.id.fob_scroll);

        View layoutTextDelegate = view.findViewById(R.id.layout_delegate);
        View textDelegate = view.findViewById(R.id.text_delegate);
        //https://www.codeleading.com/article/53194019795/
        //https://developer.android.com/guide/topics/renderscript/migrate#scripts
        //https://github.com/android/renderscript-intrinsics-replacement-toolkit
        View layoutTextDelegate3 = view.findViewById(R.id.layout_text_delegate3);
        View textDelegate3 = view.findViewById(R.id.text_delegate3);
        textDelegate3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Delegate2", Toast.LENGTH_SHORT).show();
            }
        });
        layoutTextDelegate3.post(new Runnable() {
            @Override
            public void run() {
                Rect childRect = new Rect();
                textDelegate3.getHitRect(childRect); //获取点击热区，注：热区的位置是相对于父布局的
                Rect parentRect = new Rect();
                layoutTextDelegate3.getHitRect(parentRect);
                //Log.v("Wbj", "childRect: " + childRect.toString() + ", parentRect: " + parentRect.toString());

                childRect.top = 0; //把textDelegate3的热区扩展到父布局的范围
                childRect.left = 0;
                childRect.right = parentRect.width();
                childRect.bottom = parentRect.height();
                //Log.d("Wbj", "childRect: " + childRect.toString());
                layoutTextDelegate3.setTouchDelegate(new TouchDelegate(childRect, textDelegate3));
            }
        });

        textDelegate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Delegate1", Toast.LENGTH_SHORT).show();
            }
        });
        textDelegate.post(new Runnable() {
            @Override
            public void run() {
                Rect outRect = new Rect();
                textDelegate.getHitRect(outRect);
                //Log.d("Wbj", "outRect: " + outRect.toString());
                outRect.inset(30, 30);  //验证热区向内缩进不生效，最小的热区就是View本身的尺寸，只能向外扩展生效
                //Log.d("Wbj", "outRect: " + outRect.toString());
                layoutTextDelegate.setTouchDelegate(new TouchDelegate(outRect, textDelegate));
            }
        });

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.girl_gaitubao);
        ImageView imageView = view.findViewById(R.id.image_blur);
        ImageView imageView2 = view.findViewById(R.id.image_blur2);
        int radius = 20;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RenderEffect blurEffect = RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.MIRROR);
            imageView.setRenderEffect(blurEffect);
            imageView.setImageBitmap(bitmap);

            imageView2.setRenderEffect(blurEffect);
            imageView2.setBackground(new BitmapDrawable(getResources(), bitmap));
        } else {
            imageView.setImageBitmap(Toolkit.INSTANCE.blur(bitmap, radius));
            imageView2.setBackground(new BitmapDrawable(getResources(), Toolkit.INSTANCE.blur(bitmap, radius)));
        }
    }

    @Override
    public View getBlurredView() {
        return mBlurredView;
    }

}
