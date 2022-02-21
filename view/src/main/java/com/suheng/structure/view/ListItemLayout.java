package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ListItemLayout extends RelativeLayout {
    private ImageView mLeftImage;

    public ListItemLayout(Context context) {
        super(context);
        this.init();
    }

    public ListItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public ListItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        setBackgroundColor(Color.WHITE);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int paddingStart = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, metrics);
        int paddingEnd = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, metrics);
        int paddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, metrics);
        setPadding(paddingStart, paddingTop, paddingEnd, paddingTop);

        inflate(getContext(), R.layout.view_list_item_layout, this);

        mLeftImage = (ImageView) ((ViewStub) findViewById(R.id.lil_stub_left_image)).inflate();
        RelativeLayout.LayoutParams leftImageLayoutParams = (LayoutParams) mLeftImage.getLayoutParams();
        leftImageLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        leftImageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        int dimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, metrics);
        leftImageLayoutParams.width = dimension;
        leftImageLayoutParams.height = dimension;
        mLeftImage.setLayoutParams(leftImageLayoutParams);
        mLeftImage.setImageResource(R.drawable.vector_delete);

        TextView subtitle = (TextView) ((ViewStub) findViewById(R.id.lil_stub_subtitle)).inflate();
        subtitle.setText("Subtitle");
        RelativeLayout.LayoutParams subtitleLayoutParams = (LayoutParams) subtitle.getLayoutParams();
        subtitleLayoutParams.addRule(RelativeLayout.BELOW, R.id.lil_title);
        subtitleLayoutParams.addRule(RelativeLayout.ALIGN_START, R.id.lil_title);
        subtitle.setLayoutParams(subtitleLayoutParams);

        TextView textTitle = findViewById(R.id.lil_title);
        textTitle.setText("Title");
        RelativeLayout.LayoutParams textTitleLayoutParams = (LayoutParams) textTitle.getLayoutParams();
        if (mLeftImage == null) {
            textTitleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        } else {
            textTitleLayoutParams.addRule(RelativeLayout.END_OF, mLeftImage.getId());

        }
        //textTitleLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        textTitle.setLayoutParams(textTitleLayoutParams);

        ImageView rightImage = (ImageView) ((ViewStub) findViewById(R.id.lil_stub_right_image)).inflate();
        RelativeLayout.LayoutParams rightImageLayoutParams = (LayoutParams) rightImage.getLayoutParams();
        rightImageLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        rightImageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        rightImage.setLayoutParams(rightImageLayoutParams);
        rightImage.setImageResource(R.drawable.vector_delete);


        TextView describeTitle = (TextView) ((ViewStub) findViewById(R.id.lil_stub_describe_title)).inflate();
        describeTitle.setText("DescribeTitle");
        RelativeLayout.LayoutParams describeTitleLayoutParams = (LayoutParams) describeTitle.getLayoutParams();
        describeTitleLayoutParams.addRule(RelativeLayout.START_OF, rightImage.getId());
        describeTitleLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        describeTitle.setLayoutParams(describeTitleLayoutParams);

        TextView describeSubtitle = (TextView) ((ViewStub) findViewById(R.id.lil_stub_describe_subtitle)).inflate();
        describeSubtitle.setText("DescribeSubtitle");
        RelativeLayout.LayoutParams describeSubtitleLayoutParams = (LayoutParams) describeSubtitle.getLayoutParams();
        describeSubtitleLayoutParams.addRule(RelativeLayout.BELOW, describeTitle.getId());
        describeSubtitleLayoutParams.addRule(RelativeLayout.ALIGN_END, describeTitle.getId());
        describeSubtitle.setLayoutParams(describeSubtitleLayoutParams);
    }

}
