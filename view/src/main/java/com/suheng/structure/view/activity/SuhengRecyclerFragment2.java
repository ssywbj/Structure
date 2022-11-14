package com.suheng.structure.view.activity;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.renderscript.Toolkit;
import com.suheng.structure.view.GridItemDecoration;
import com.suheng.structure.view.R;
import com.suheng.structure.view.utils.DateUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class SuhengRecyclerFragment2 extends SuhengBaseFragment {
    private static final String TAG = SuhengRecyclerFragment2.class.getSimpleName();
    public static final int SPAN_COUNT = 3;
    public static final int SPAN_SPACE = 1;
    private ContentAdapter mContentAdapter;
    private final List<Object> mDataList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_picture_manager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.view_picture_manager_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        Drawable drawable = AppCompatResources.getDrawable(mContext, R.drawable.recycler_view_linear_divide_line);
        if (drawable != null) {
            DividerItemDecoration itemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
            itemDecoration.setDrawable(drawable);
        }
        //final int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        GridItemDecoration itemDecoration = new GridItemDecoration(gridLayoutManager, SPAN_SPACE);
        itemDecoration.setColor(Color.RED);
        mRecyclerView.addItemDecoration(itemDecoration);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                RecyclerView.Adapter<?> adapter = mRecyclerView.getAdapter();
                if (adapter == null) {
                    return 1;
                }

                int itemViewType = adapter.getItemViewType(position);
                return ((itemViewType == ContentAdapter.VIEW_TYPE_FOOTER) || (itemViewType == ContentAdapter.VIEW_TYPE_TITLE))
                        ? gridLayoutManager.getSpanCount() : 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mContentAdapter = new ContentAdapter());

        ActivityResultLauncher<String> activityResult = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result.equals(true)) {
                        this.queryPictures();
                    } else {
                        Toast.makeText(getContext(), "reject permission", Toast.LENGTH_SHORT).show();
                    }
                });
        activityResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

        if (mContext instanceof BlurActivity) {
            mActivity = (BlurActivity) mContext;
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int rdx, int rdy) {
                    super.onScrolled(recyclerView, rdx, rdy);
                    //updateBlurViewBackground(mActivity.getTopViewBlur(), mRecyclerView);
                    //updateBlurViewBackground(mActivity.getViewBlur(), mRecyclerView);
                }
            });
        }

    }

    private BlurActivity mActivity;

    private void updateBlurViewBackground(View viewBlur, View viewBlurred) {
        int[] location = new int[2];
        viewBlur.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        viewBlurred.getLocationOnScreen(location);
        int x1 = location[0];
        int y1 = location[1];

        int width = viewBlur.getWidth();
        int height = viewBlur.getHeight();
        int bitmapWidth = (int) Math.ceil(1f * width / mScaleFactor);
        int bitmapHeight = (int) Math.ceil(1f * height / mScaleFactor);
        if (bitmapWidth == 0 || bitmapHeight == 0) {
            return;
        }
        Log.d(TAG, "width: " + width + ", height: " + height + ", bitmapWidth: " + bitmapWidth + ", bitmapHeight: " + bitmapHeight);
        if (mViewBlurBitmap == null) {
            mViewBlurBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_4444);
            mViewBlurCanvas = new Canvas(mViewBlurBitmap);
            float scale = 1f / mScaleFactor;
            float dx = x1 - x;
            float dy = y1 - y;
            mViewBlurCanvas.scale(scale, scale);
            mViewBlurCanvas.translate(dx, dy);
            Log.d(TAG, "x: " + x + ", y: " + y + ", x1: " + x1 + ", y1: " + y1 + ", scale: " + scale + ", dx: " + dx + ", dy: " + dy);
        }
        viewBlurred.draw(mViewBlurCanvas);

        Bitmap blurredBitmap = Toolkit.INSTANCE.blur(mViewBlurBitmap, mRadius);

        if (mViewBlurBg == null) {
            mViewBlurBg = new BitmapDrawable(getResources(), blurredBitmap);
            viewBlur.setBackground(mViewBlurBg);
        } else {
            Bitmap bgBitmap = mViewBlurBg.getBitmap();
            if (bgBitmap == null) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mViewBlurBg.setBitmap(blurredBitmap);
                viewBlur.invalidateDrawable(mViewBlurBg);
                if (!bgBitmap.isRecycled()) {
                    Log.d(TAG, "recycle, bgBitmap: " + bgBitmap);
                    bgBitmap.recycle();
                }
            } else {
                try {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(blurredBitmap.getByteCount());
                    blurredBitmap.copyPixelsToBuffer(byteBuffer);
                    Log.d(TAG, "run, 333333333: " + bgBitmap + ", thread: " + Thread.currentThread().getName());
                    bgBitmap.eraseColor(Color.TRANSPARENT);
                    bgBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(byteBuffer.array()));
                } catch (Exception e) {
                    Log.e(TAG, "copy blurredBitmap bitmap fail!", e);
                } finally {
                    if (!blurredBitmap.isRecycled()) {
                        blurredBitmap.recycle();
                    }
                }
            }
        }
    }

    private Canvas mViewBlurCanvas;
    private Bitmap mViewBlurBitmap;
    private BitmapDrawable mViewBlurBg;
    private int mScaleFactor = 6;
    private int mRadius = 20;

    public void setScaleFactor(int scaleFactor) {
        mScaleFactor = scaleFactor;
    }

    public void setRadius(int radius) {
        mRadius = radius;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataList.clear();
    }

    private void queryPictures() {
        List<ImageInfo> imageInfoList = new ArrayList<>();
        //String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT};//查询字段
        String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.ORIENTATION, MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT, MediaStore.Images.Media.SIZE};//查询字段
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
        //Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, sortOrder);

        String absolutePath = "/storage/emulated/0/DCIM/Camera/20161209_120251.jpg";//查询某一张图片
        absolutePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();//查询某个目录下的图片
        absolutePath = "";//查询所有目录下的图片
        String selection = MediaStore.Images.Media.DATA + " like ?";//查询条件
        String[] selectionArgs = {absolutePath + "%"};//查询目录

        Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, selectionArgs, sortOrder); //columns传空表示查询所有字段
        Log.d(TAG, "cursor.getCount(): " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                String dirName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                int width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                long dateModified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)) * 1000;//*1000:秒转化为毫秒
                int orientation = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION));
                //Log.v("Wbj", "queryPictures: " + imagePath + ", dirName： " + dirName + ", width: " + width + ", height: " + height);

                ImageInfo imageInfo = new ImageInfo(imagePath, dirName, width, height, size, dateModified, orientation);
                imageInfoList.add(imageInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();

        final int contentLength = imageInfoList.size();
        if (contentLength > 0) {
            boolean isNotSameDay;
            //List<ImageInfo> dateImages = null;
            TitleInfo imageTitle = null;
            /*
             * 算法：在for循环中，如果Item集合为空，那么先加入日期Title项再加入第一条数据项；往后的数据项与
             * 前一项比较，如果是同一日期则直接加在集合后面，如果是不同日期则先加入日期Title项再加入数据项。
             */
            for (ImageInfo imageInfo : imageInfoList) {
                imageInfo.setDate(DateUtils.formatDateTime(getContext(), imageInfo.getDateModified(), DateUtils.FORMAT_SHOW_YEAR));
                if (mDataList.size() > 0) {
                    isNotSameDay = !DateUtil.isSameDay(((ImageInfo) mDataList.get(mDataList.size() - 1))
                            .getDateModified(), imageInfo.getDateModified());//mDataList.get(size - 1)：取出前一项数据
                } else {
                    isNotSameDay = true;
                }
                if (isNotSameDay) { //如果不是同一天
                    imageTitle = new TitleInfo(imageInfo.getDate());
                    mDataList.add(imageTitle); //先加入日期Title项
                }
                mDataList.add(imageInfo); //再加入数据项
            }

            imageInfoList.clear();

            mDataList.add(new FooterInfo(contentLength)); //再加入数据项
        } else {
            Log.d("Wbj", "没有数据了");
        }

        //mContentAdapter.notifyItemRangeChanged(0, imageInfoList.size());
        mContentAdapter.notifyDataSetChanged();
    }

    @Override
    public View getBlurredView() {
        return mRecyclerView;
    }

    private boolean mEditMode;

    private class ContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int VIEW_TYPE_FOOTER = 1;
        private static final int VIEW_TYPE_TITLE = 2;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_TITLE) {
                return new TitleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_picture_manager_adt_title, parent, false));
            } else if (viewType == VIEW_TYPE_FOOTER) {
                return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_picture_manager_adt_footer, parent, false));
            } else {
                return new ContentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_picture_manager_adt, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Object object = mDataList.get(position);

            if ((holder instanceof FooterHolder) && (object instanceof FooterInfo)) {
                ((FooterHolder) holder).mTextCount.setText(getString(R.string.picture_bottom_number, ((FooterInfo) object).getCount()));
            }

            if ((holder instanceof TitleHolder) && (object instanceof TitleInfo)) {
                final TitleHolder titleHolder = (TitleHolder) holder;
                titleHolder.mTextDate.setText(((TitleInfo) object).getTitle());
                titleHolder.mCheckBox.setVisibility(View.GONE);
            }

            if ((holder instanceof ContentHolder) && (object instanceof ImageInfo)) {
                ContentHolder contentHolder = (ContentHolder) holder;
                Glide.with(SuhengRecyclerFragment2.this).load(((ImageInfo) object).getPath()).into(contentHolder.mImageView);

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mEditMode = true;
                        notifyItemChanged(position);
                        return mEditMode;
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mEditMode){
                            //notifyItemChanged(position);
                            notifyItemChanged(position, object);
                            Log.d("FobRecyclerFrg", "ContentHolder, holder: " + holder);
                        }
                    }
                });

            }
        }

        @Override
        public int getItemViewType(int position) {
            Object object = mDataList.get(position);
            if (object instanceof TitleInfo) {
                return VIEW_TYPE_TITLE;
            } else if (object instanceof FooterInfo) {
                return VIEW_TYPE_FOOTER;
            } else {
                return 0;
            }
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }

    private final static class ContentHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;

        public ContentHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.apma_image_view);
        }
    }

    private final static class FooterHolder extends RecyclerView.ViewHolder {
        TextView mTextCount;

        public FooterHolder(@NonNull View itemView) {
            super(itemView);
            mTextCount = itemView.findViewById(R.id.apmaf_text_count);
        }
    }

    private final static class TitleHolder extends RecyclerView.ViewHolder {
        TextView mTextDate;
        CheckBox mCheckBox;

        public TitleHolder(@NonNull View itemView) {
            super(itemView);
            mTextDate = itemView.findViewById(R.id.apmat_text_date);
            mCheckBox = itemView.findViewById(R.id.checkbox);
        }
    }

    private static class ImageInfo {
        private final String path;
        private final String dirName;
        private final int width;
        private final int height;
        private final long size;
        private final long dateModified;
        private final int orientation;

        private String date;

        public ImageInfo(String path, String dirName, int width, int height, long size, long dateModified, int orientation) {
            this.path = path;
            this.dirName = dirName;
            this.width = width;
            this.height = height;
            this.size = size;
            this.dateModified = dateModified;
            this.orientation = orientation;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public long getDateModified() {
            return dateModified;
        }

        public int getOrientation() {
            return orientation;
        }

        public String getPath() {
            return path;
        }

        public String getDirName() {
            return dirName;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public long getSize() {
            return size;
        }
    }

    private static class TitleInfo {
        private final String title;

        TitleInfo(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    private static class FooterInfo {
        private final int count;

        FooterInfo(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

}
