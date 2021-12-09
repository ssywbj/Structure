package com.suheng.structure.view.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.view.LetterSelectorLayout;
import com.suheng.structure.view.R;
import com.suheng.structure.view.adapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LetterSelectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_select_layout);

        List<String> letterList = new ArrayList<>(Arrays.asList(LetterSelectorLayout.LETTERS));

        LetterSelectorLayout letterSelectorLayout = findViewById(R.id.letter_selector_layout);
        letterSelectorLayout.setLetters(letterList);
        //letterSelectorLayout.setSelectedPosition(0);

        RecyclerView recyclerView = findViewById(R.id.letter_layout_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ContentAdapter contentAdapter = new ContentAdapter(letterList);
        recyclerView.setAdapter(contentAdapter);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                LinearLayoutManager linearLayoutManager = null;
                if (layoutManager instanceof LinearLayoutManager) {
                    linearLayoutManager = (LinearLayoutManager) layoutManager;
                }
                if (linearLayoutManager == null) {
                    return;
                }

                int fvip = linearLayoutManager.findFirstVisibleItemPosition();
                int fcvip = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                int lvip = linearLayoutManager.findLastVisibleItemPosition();
                int lcvip = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                Log.d(LetterSelectorLayout.TAG, "firstVisibleItemPosition: " + fvip + ", firstCompletelyVisibleItemPosition: "
                        + fcvip + ", lastVisibleItemPosition: " + lvip + ", lastCompletelyVisibleItemPosition: " + lcvip);
                //letterSelectorLayout.setSelectedPosition(fvip);
                letterSelectorLayout.setLastVisibleItemPosition(lvip);
                letterSelectorLayout.setSelectedLetter(letterList.get(fvip));
            });
        }

        letterSelectorLayout.setOnTouchLetterListener((letter, pst) -> {
            Log.i("LetterSelectActivity", "onTouchLetter: " + letter + ", " + pst);
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                linearLayoutManager.scrollToPositionWithOffset(pst, 0);
                //linearLayoutManager.setStackFromEnd(true);
            }
        });
    }

    private static final class ContentAdapter extends RecyclerAdapter<String, ContentHolder> {

        protected ContentAdapter(List<String> dataList) {
            super(dataList);
        }

        @Override
        public void onBindViewHolder(@NonNull ContentHolder holder, int pst, String data) {
            holder.textName.setText(data);
            if (pst % 2 == 0) {
                holder.itemView.setBackgroundColor(Color.parseColor("#CCCCCC"));
            } else {
                holder.itemView.setBackgroundColor(Color.parseColor("#AAAAAA"));
            }
        }

        @NonNull
        @Override
        public ContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContentHolder(parent);
        }
    }

    private final static class ContentHolder extends RecyclerAdapter.Holder {
        TextView textName;

        ContentHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.activity_main_adt);
            textName = itemView.findViewById(R.id.view_main_rvt_title);

            mIsSetOnClickListener = true;
            mIsSetOnLongClickListener = true;
        }
    }

}
