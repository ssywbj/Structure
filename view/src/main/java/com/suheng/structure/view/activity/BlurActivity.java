package com.suheng.structure.view.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
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

        View rootLayout = findViewById(R.id.root_layout);
        View parentDelegate = findViewById(R.id.layout_delegate);
        View textDelegate = findViewById(R.id.text_delegate);
        //https://www.codeleading.com/article/53194019795/
        //https://developer.android.com/guide/topics/renderscript/migrate#scripts
        //https://github.com/android/renderscript-intrinsics-replacement-toolkit
        /*parentDelegate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BlurActivity.this, "parentDelegate", Toast.LENGTH_SHORT).show();
            }
        });*/
        /*parentDelegate.post(new Runnable() {
            @Override
            public void run() {
                Rect outRect = new Rect();
                parentDelegate.getHitRect(outRect);
                Log.d("Wbj", "run: " + outRect.toShortString());
                outRect.left += 200;
                outRect.right -= 200;
                Log.d("Wbj", "run: " + outRect.toShortString());
                ((View) parentDelegate.getParent()).setTouchDelegate(new TouchDelegate(outRect, parentDelegate));
            }
        });*/

        textDelegate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BlurActivity.this, "textDelegate1", Toast.LENGTH_SHORT).show();
            }
        });
        textDelegate.post(new Runnable() {
            @Override
            public void run() {
                Rect outRect = new Rect();
                textDelegate.getHitRect(outRect);
                Log.d("Wbj", "run: " + outRect.toShortString());
                outRect.left -= 100;
                outRect.right += 100;
                Log.d("Wbj", "run: " + outRect.toShortString());
                parentDelegate.setTouchDelegate(new TouchDelegate(outRect, textDelegate));
            }
        });

        View textDelegate2 = findViewById(R.id.text_delegate2);
        textDelegate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BlurActivity.this, "textDelegate2", Toast.LENGTH_SHORT).show();
            }
        });

        View textDelegate3 = findViewById(R.id.text_delegate3);
        textDelegate3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BlurActivity.this, "textDelegate3", Toast.LENGTH_SHORT).show();
            }
        });
        rootLayout.post(new Runnable() {
            @Override
            public void run() {
                Rect outRect = new Rect();
                textDelegate3.getHitRect(outRect);
                Log.d("Wbj", "run: " + outRect.toShortString());
                outRect.left -= 100;
                outRect.right += 100;
                Log.d("Wbj", "run: " + outRect.toShortString());
                rootLayout.setTouchDelegate(new TouchDelegate(outRect, textDelegate3));
            }
        });

        Toolkit toolkit = Toolkit.INSTANCE;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.girl_gaitubao);
        ImageView imageView = findViewById(R.id.image_blur);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            imageView.setRenderEffect(RenderEffect.createBlurEffect(20, 20, Shader.TileMode.MIRROR));
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageBitmap(Toolkit.INSTANCE.blur(bitmap, 20));
        }*/

        //imageView.setImageBitmap(bitmap);
        imageView.setImageBitmap(toolkit.blur(bitmap, 20));
    }

}