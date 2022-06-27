package com.suheng.structure.view.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.renderscript.Toolkit;
import com.suheng.structure.view.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FobRecyclerFrg extends FobBaseFrg {
    private Bitmap mBitmapBlur;
    private RecyclerView mRecyclerView;
    private ImageView mImageSub;
    private final Rect mRect = new Rect();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fob_recycler, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBitmapBlur = Toolkit.INSTANCE.blur(BitmapFactory.decodeResource(getResources(), R.drawable.beauty2), 16);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.fob_recycler);
        mImageSub = view.findViewById(R.id.fob_image_sub);
        this.initListView();
    }

    private void initListView() {
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new GridItemDecoration(1));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                RecyclerView.Adapter<?> adapter = mRecyclerView.getAdapter();
                if (adapter == null) {
                    return 0;
                }

                int itemViewType = adapter.getItemViewType(position);
                return (itemViewType == ContentAdapter.VIEW_TYPE_FOOTER) ? gridLayoutManager.getSpanCount() : 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);

        ContentAdapter adapter = new ContentAdapter();
        mRecyclerView.setAdapter(adapter);

        //BlurActivity.getViewLocation(mRecyclerView);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadViewBitmap(mRecyclerView);
                mImageSub.setBackground(new BitmapDrawable(getResources(), bitmap));

                Rect rect = new Rect();
                mRecyclerView.getHitRect(rect);
                Log.d("Wbj", "run, view bitmap: " + bitmap + ", rect: " + rect.toString());

                //BlurActivity.getViewLocation(mRecyclerView);
            }
        });

        mImageSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = loadViewBitmap(mRecyclerView);
                mImageSub.setBackground(null);
                mImageSub.setBackground(new BitmapDrawable(getResources(), bitmap));
            }
        });
    }

    @Override
    public View getBlurredView() {
        return mRecyclerView;
    }

    public static Bitmap loadViewBitmap(View view) {
        if (view == null) {
            return null;
        }
        Bitmap screenshot = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(screenshot);
        view.draw(canvas);
        return screenshot;
    }

    public static Bitmap loadViewBitmap(View view, Rect rect) {
        if (view == null) {
            return null;
        }
        int width = view.getWidth();
        int height = view.getHeight();
        Bitmap screenshot = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(screenshot);
        Log.d("Wbj", "loadViewBitmap, width: " + width + ", height: " + height+ ", canvas.getWidth: " + canvas.getWidth()+ ", canvas.getHeight: " + canvas.getHeight());
        //canvas.clipRect(rect);
        view.draw(canvas);
        return screenshot;
    }

    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        byte[] result = output.toByteArray();
        try {
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public void saveFile(File file, byte[] data) {
        try {
            OutputStream out = new FileOutputStream(file);
            out.write(data);
            out.close();
        } catch (IOException e) {
            Log.d("Wbj", "写入文件时失败", e);
        }
    }

    private class ContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public static final int VIEW_TYPE_CONTENT = 0;
        public static final int VIEW_TYPE_FOOTER = 1;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_FOOTER) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_fob_recycler_adt_empty, parent, false);
                return new EmptyHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_fob_recycler_adt, parent, false);
                return new ContentHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ContentHolder) {
                ((ContentHolder) holder).mImageView.setImageResource(R.drawable.beauty2);
                //((ContentHolder) holder).mImageView.setImageBitmap(mBitmapBlur);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return VIEW_TYPE_FOOTER;
            } else {
                return VIEW_TYPE_CONTENT;
            }
        }

        @Override
        public int getItemCount() {
            return 42;
        }
    }

    private final static class ContentHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;

        public ContentHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.apma_image_view);
        }
    }

    private final static class EmptyHolder extends RecyclerView.ViewHolder {
        public EmptyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private static class GridItemDecoration extends RecyclerView.ItemDecoration {

        private final int mSpace;

        public GridItemDecoration(int space) {
            mSpace = space;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mSpace, mSpace, mSpace, mSpace);
        }
    }

}
