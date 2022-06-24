package com.suheng.structure.view.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.renderscript.Toolkit;
import com.suheng.structure.view.R;

public class BlurActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur);

        View layoutTextDelegate = findViewById(R.id.layout_delegate);
        View textDelegate = findViewById(R.id.text_delegate);
        //https://www.codeleading.com/article/53194019795/
        //https://developer.android.com/guide/topics/renderscript/migrate#scripts
        //https://github.com/android/renderscript-intrinsics-replacement-toolkit
        View layoutTextDelegate3 = findViewById(R.id.layout_text_delegate3);
        View textDelegate3 = findViewById(R.id.text_delegate3);
        textDelegate3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BlurActivity.this, "Delegate2", Toast.LENGTH_SHORT).show();
            }
        });
        layoutTextDelegate3.post(new Runnable() {
            @Override
            public void run() {
                Rect childRect = new Rect();
                textDelegate3.getHitRect(childRect); //获取点击热区，注：热区的位置是相对于父布局的
                Rect parentRect = new Rect();
                layoutTextDelegate3.getHitRect(parentRect);
                Log.v("Wbj", "childRect: " + childRect.toString() + ", parentRect: " + parentRect.toString());

                childRect.top = 0; //把textDelegate3的热区扩展到父布局的范围
                childRect.left = 0;
                childRect.right = parentRect.width();
                childRect.bottom = parentRect.height();
                Log.d("Wbj", "childRect: " + childRect.toString());
                layoutTextDelegate3.setTouchDelegate(new TouchDelegate(childRect, textDelegate3));
            }
        });

        textDelegate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BlurActivity.this, "Delegate1", Toast.LENGTH_SHORT).show();
            }
        });
        textDelegate.post(new Runnable() {
            @Override
            public void run() {
                Rect outRect = new Rect();
                textDelegate.getHitRect(outRect);
                Log.d("Wbj", "outRect: " + outRect.toString());
                outRect.inset(30, 30);  //验证热区向内缩进不生效，最小的热区就是View本身的尺寸，只能向外扩展生效
                Log.d("Wbj", "outRect: " + outRect.toString());
                layoutTextDelegate.setTouchDelegate(new TouchDelegate(outRect, textDelegate));
            }
        });

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.girl_gaitubao);
        ImageView imageView = findViewById(R.id.image_blur);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            imageView.setRenderEffect(RenderEffect.createBlurEffect(20, 20, Shader.TileMode.MIRROR));
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageBitmap(Toolkit.INSTANCE.blur(bitmap, 20));
        }
    }

}