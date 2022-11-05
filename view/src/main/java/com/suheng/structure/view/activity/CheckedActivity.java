package com.suheng.structure.view.activity;

import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.res.XmlResourceParser;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.PathParser;

import com.suheng.structure.view.R;

import org.xmlpull.v1.XmlPullParser;

public class CheckedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked);

        //AnimCheckBox animCB = findViewById(R.id.anim_check_box);
        //animCB.setChecked(false);
        ImageView imageCheckedDrawable = findViewById(R.id.image_checked_drawable);
        //imageCheckedDrawable.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.checkbox_checked));
        //imageCheckedDrawable.setImageDrawable(new CheckedDrawable(Color.RED));

        /*RadioGroup radioGroup = findViewById(R.id.svg_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //Log.i(AnimRadioButton.TAG, "RadioGroup, checkedId: " + checkedId);
            }
        });
        animCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroup.clearCheck();
            }
        });*/

        this.evlaAnim();
        this.pathAnim();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        menu.findItem(R.id.menu_copy).setCheckable(true).setChecked(true);
        menu.findItem(R.id.menu_setting).setCheckable(true).setChecked(false);
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Toast.makeText(this, "item = " + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
        //return true;
    }*/

    private void evlaAnim() {
        /*PropertyValuesHolder pvhBorder = PropertyValuesHolder.ofObject("111", new TypeEvaluator<Border>() {
            @Override
            public Border evaluate(float fraction, Border startValue, Border endValue) {
                Log.d("Wbj", "evaluate, startValue: " + startValue + ", endValue: " + endValue + ", fraction: " + fraction);

                int delta = (int) (fraction * 13);
                return new Border(startValue.left + delta, startValue.top + delta, startValue.radius + delta);
            }
        }, new Border(1, 2, 0), new Border(11, 12, 10));
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(pvhBorder);*/
        ValueAnimator animator = ValueAnimator.ofObject(new BorderEvaluator(), new Border(1, 2, 0), new Border(11, 12, 10));
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object obj = animation.getAnimatedValue();
                //Log.v("Wbj", "onAnimationUpdate, obj: " + obj);
            }
        });
        animator.start();
    }

    private void pathAnim() {
        XmlResourceParser parser = getResources().getXml(R.xml.wbj_svg2);
        Path path = null;
        try {
            RectF rectF = new RectF();

            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();

                    if ("path".equals(tagName)) {
                        path = PathParser.createPathFromPathData(parser.getAttributeValue("http://schemas.android.com/apk/res/android", "pathData"));
                        path.computeBounds(rectF, true);
                        //Log.v("Wbj", "path: " + path);
                    }
                }

                parser.next();
            }
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (path != null) {
            StringBuilder sb = new StringBuilder();
            PropertyValuesHolder pvhPath = PropertyValuesHolder.ofMultiInt("path", path); //描述SVG路径的坐标变化过程
            ValueAnimator animatorPath = ValueAnimator.ofPropertyValuesHolder(pvhPath);
            animatorPath.setDuration(1000);
            animatorPath.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Object obj = animation.getAnimatedValue();
                    if (obj instanceof int[]) {
                        int[] pathArray = (int[]) obj;
                        int length = pathArray.length;
                        if (length >= 2) {
                            sb.delete(0, sb.length());
                            sb.append("(").append(pathArray[0]).append(", ").append(pathArray[1]).append(")"); //数组长度为2，第一个值是x坐标，第二个值是y坐标
                            Log.v("Wbj", "pvhPath onAnimationUpdate, obj: " + length + ", pivot: " + sb);
                        }
                    }
                }
            });
            animatorPath.start();
        }
    }

    final static class Border {
        int left, top;
        float radius;

        public Border(int left, int top, float radius) {
            this.left = left;
            this.top = top;
            this.radius = radius;
        }

        @Override
        public String toString() {
            return "Border{" +
                    "left=" + left +
                    ", top=" + top +
                    ", radius=" + radius +
                    '}';
        }
    }

    final static class BorderEvaluator implements TypeEvaluator<Border> {

        @Override
        public Border evaluate(float fraction, Border startValue, Border endValue) {
            //Log.d("Wbj", "evaluate, startValue: " + startValue + ", endValue: " + endValue + ", fraction: " + fraction);

            int delta = (int) (fraction * 13);
            return new Border(startValue.left + delta, startValue.top + delta, startValue.radius + delta);
        }
    }

}
