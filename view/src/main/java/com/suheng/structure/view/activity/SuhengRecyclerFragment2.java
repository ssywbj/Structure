package com.suheng.structure.view.activity;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.suheng.structure.view.GridItemDecoration;
import com.suheng.structure.view.R;
import com.suheng.structure.view.adapter.RecyclerAdapter;
import com.suheng.structure.view.utils.DateUtil;
import com.suheng.structure.view.utils.RealBlur;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuhengRecyclerFragment2 extends SuhengBaseFragment {
    public static final int SPAN_COUNT = 3;
    public static final int SPAN_SPACE = 1;
    private ContentAdapter mContentAdapter;
    private final List<ImageInfo> mDataList = new ArrayList<>();
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
        mRecyclerView.setAdapter(mContentAdapter = new ContentAdapter(mDataList));

        ActivityResultLauncher<String> activityResult = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result.equals(true)) {
                        this.queryPictures();
                    } else {
                        Toast.makeText(getContext(), "reject permission", Toast.LENGTH_SHORT).show();
                    }
                });
        activityResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

        View topViewBlur = view.findViewById(R.id.frg_fob_image_blur);

        if (mContext instanceof BlurActivity) {
            BlurActivity activity = (BlurActivity) mContext;
            RealBlur realBlur = activity.getDynamicBlur();
            View viewBlur = activity.getViewBlur();
            realBlur.setViewBlurredAndBlur(mRecyclerView, viewBlur);

            /*mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Log.d("Wbj", "onScrollChanged: " + mRecyclerView);
                    realBlur.updateBlurViewBackground(true);
                }
            });*/

            ViewTreeObserver viewTreeObserver = mRecyclerView.getViewTreeObserver();
            viewTreeObserver.addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                @Override
                public void onDraw() {
                    Log.d("Wbj", "viewTreeObserver, onDraw: " + mRecyclerView);
                    //realBlur.updateBlurViewBackground(true);
                }
            });
            /*viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    Log.d("Wbj", "onPreDraw: " + mRecyclerView);
                    //realBlur.updateBlurViewBackground(true);
                    return true;
                }
            });*/

            viewTreeObserver.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    Log.d("Wbj", "viewTreeObserver, onScrollChanged: " + mRecyclerView);
                    //realBlur.updateBlurViewBackground(false);

                    int width = mRecyclerView.getWidth();
                    int height = mRecyclerView.getHeight();
                    if (width == 0 || height == 0) {
                        return;
                    }

                    if (canvas == null) {
                        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
                        canvas = new Canvas(bitmap);
                        clipRect.set(0, 0, topViewBlur.getWidth(), topViewBlur.getHeight());
                        //canvas.clipRect(clipRect);
                        //float sy = 1.0f * mRecyclerView.getHeight() / topViewBlur.getHeight();
                        //Log.d("Wbj", "onClick, clipRect: " + clipRect.toShortString() + ", " + mRecyclerView.getHeight() + "---" + topViewBlur.getHeight() + ", sy: " + sy);
                    }

                    //bitmap.eraseColor(Color.TRANSPARENT);
                    //mRecyclerView.draw(canvas);
                    //topViewBlur.setBackground(new BitmapDrawable(getResources(), Toolkit.INSTANCE.blur(bitmap, 20)));
                    //topViewBlur.setBackground(new BitmapDrawable(getResources(), bitmap));

                    /*int[] location = new int[2];
                    topViewBlur.getLocationOnScreen(location);
                    int x = location[0];
                    int y = location[1];
                    Rect rectBlur = new Rect();
                    rectBlur.left = x;
                    rectBlur.top = y;
                    rectBlur.right = rectBlur.left + topViewBlur.getWidth();
                    rectBlur.bottom = rectBlur.top + topViewBlur.getHeight();*/

                    /*int[] location = new int[2];
                    topViewBlur.getLocationInWindow(location);
                    int x = location[0];
                    int y = location[1];
                    Rect rectBlur = new Rect();
                    rectBlur.left = x;
                    rectBlur.top = y + topViewBlur.getHeight();
                    rectBlur.right = rectBlur.left + topViewBlur.getWidth();
                    rectBlur.bottom = rectBlur.top + topViewBlur.getHeight();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Bitmap bitmap = Bitmap.createBitmap(topViewBlur.getWidth(), topViewBlur.getHeight(), Bitmap.Config.ARGB_4444);
                        PixelCopy.request(activity.getWindow(), rectBlur, bitmap, new PixelCopy.OnPixelCopyFinishedListener() {
                            @Override
                            public void onPixelCopyFinished(int copyResult) {
                                Log.d("Wbj", "onPixelCopyFinished, copyResult: " + copyResult);
                                topViewBlur.setBackground(new BitmapDrawable(getResources(), bitmap));
                            }
                        }, topViewBlur.getHandler());
                    }*/
                }
            });

            topViewBlur.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] location = new int[2];
                    topViewBlur.getLocationInWindow(location);
                    int x = location[0];
                    int y = location[1];
                    Rect rectBlur = new Rect();
                    rectBlur.left = x;
                    rectBlur.top = topViewBlur.getHeight() + y;
                    rectBlur.right = rectBlur.left + topViewBlur.getWidth();
                    rectBlur.bottom = rectBlur.top + topViewBlur.getHeight();
                    Log.d("Wbj", "onClick, rectBlur: " + rectBlur.toString());
                    mRecyclerView.getLocationOnScreen(location);
                    int x1 = location[0];
                    int y1 = location[1];

                    /*Bitmap bitmap = Bitmap.createBitmap(mRecyclerView.getWidth(), mRecyclerView.getHeight(), Bitmap.Config.ARGB_4444);
                    Canvas canvas = new Canvas(bitmap);
                    RectF clipRect = new RectF();
                    clipRect.set(0, 0, topViewBlur.getWidth(), topViewBlur.getHeight());
                    float sy = 1.0f * mRecyclerView.getHeight() / topViewBlur.getHeight();
                    Log.d("Wbj", "onClick, clipRect: " + clipRect.toShortString() + ", " + mRecyclerView.getHeight()
                            + "---" + topViewBlur.getHeight() + ", sy: " + sy);
                    canvas.scale(1, sy);
                    canvas.clipRect(clipRect);
                    mRecyclerView.draw(canvas);*/

                    /*Bitmap bitmap = Bitmap.createBitmap(mRecyclerView.getWidth(), mRecyclerView.getHeight(), Bitmap.Config.ARGB_4444);
                    Canvas canvas = new Canvas(bitmap);
                    RectF clipRect = new RectF();
                    int top = 20;
                    clipRect.set(0, top, viewBlur.getWidth(), top + viewBlur.getHeight());
                    float sy = 1.0f * mRecyclerView.getHeight() / viewBlur.getHeight();
                    Log.d("Wbj", "onClick, clipRect: " + clipRect.toShortString() + ", " + mRecyclerView.getHeight()
                            + "---" + viewBlur.getHeight() + ", sy: " + sy);
                    canvas.scale(1, sy);
                    //canvas.translate(0, top);
                    canvas.clipRect(clipRect);
                    mRecyclerView.draw(canvas);*/

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Bitmap bitmap = Bitmap.createBitmap(topViewBlur.getWidth(), topViewBlur.getHeight(), Bitmap.Config.ARGB_4444);
                        PixelCopy.request(activity.getWindow(), rectBlur, bitmap, new PixelCopy.OnPixelCopyFinishedListener() {
                            @Override
                            public void onPixelCopyFinished(int copyResult) {
                                Log.d("Wbj", "onPixelCopyFinished, copyResult: " + copyResult);
                                topViewBlur.setBackground(new BitmapDrawable(getResources(), bitmap));
                            }
                        }, topViewBlur.getHandler());
                    }

                    /*Bitmap bitmap = Bitmap.createBitmap(mRecyclerView.getWidth(), mRecyclerView.getHeight(), Bitmap.Config.ARGB_4444);
                    Canvas canvas = new Canvas(bitmap);
                    RectF clipRect = new RectF();
                    clipRect.set(0, 0, topViewBlur.getWidth(), topViewBlur.getHeight());
                    float sy = 1.0f * mRecyclerView.getHeight() / topViewBlur.getHeight();
                    Log.d("Wbj", "onClick, clipRect: " + clipRect.toShortString() + ", " + mRecyclerView.getHeight()
                            + "---" + topViewBlur.getHeight() + ", sy: " + sy);
                    canvas.scale(1, sy);
                    canvas.clipRect(clipRect);
                    mRecyclerView.draw(canvas);*/

                    topViewBlur.setBackground(new BitmapDrawable(getResources(), bitmap));

                    /*Bitmap blurredBitmap = Toolkit.INSTANCE.blur(blurBitmap, 25);
                    if (!blurBitmap.isRecycled()) {
                        blurBitmap.recycle();
                    }*/

                    /*Bitmap bitmap = Bitmap.createBitmap(mRecyclerView.getWidth(), mRecyclerView.getHeight(), Bitmap.Config.ARGB_4444);
                    Canvas canvas = new Canvas(bitmap);
                    mRecyclerView.draw(canvas);
                    Bitmap blurBitmap = Bitmap.createBitmap(bitmap, Math.abs(x - x1)
                            , Math.abs(y - y1), viewBlur.getWidth(), viewBlur.getHeight());

                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    Bitmap blurredBitmap = Toolkit.INSTANCE.blur(blurBitmap, 25);
                    if (!blurBitmap.isRecycled()) {
                        blurBitmap.recycle();
                    }

                    topViewBlur.setBackground(new BitmapDrawable(getResources(), blurredBitmap));*/
                }
            });
        }

    }

    Canvas canvas = null;
    Bitmap bitmap;
    RectF clipRect = new RectF();
    private final ExecutorService mThreadPool = Executors.newFixedThreadPool(3);

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
        absolutePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();//查询某个目录下的所有图片
        String selection = MediaStore.Images.Media.DATA + " like ?";//查询条件
        String[] selectionArgs = {absolutePath + "%"};//查询目录
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, selectionArgs, sortOrder); //columns传空表示查询所有字段
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
            List<ImageInfo> dateImages = null;
            ImageInfo imageTitle = null;
            /*
             * 算法：在for循环中，如果Item集合为空，那么先加入日期Title项再加入第一条数据项；往后的数据项与
             * 前一项比较，如果是同一日期则直接加在集合后面，如果是不同日期则先加入日期Title项再加入数据项。
             */
            for (ImageInfo currentImage : imageInfoList) {
                currentImage.setDate(DateUtils.formatDateTime(getContext(), currentImage.getDateModified(), DateUtils.FORMAT_SHOW_YEAR));
                if (mDataList.size() > 0) {
                    isNotSameDay = !DateUtil.isSameDay(mDataList.get(mDataList.size() - 1).getDateModified()
                            , currentImage.getDateModified());//mDataList.get(size - 1)：取出前一项数据
                } else {
                    isNotSameDay = true;
                }
                if (isNotSameDay) { //如果不是同一天
                    imageTitle = new ImageInfo(currentImage.getDate(), ContentAdapter.VIEW_TYPE_TITLE);
                    mDataList.add(imageTitle); //先加入日期Title项

                    dateImages = new ArrayList<>();
                    //mMapDateImage.put(currentImage.getDate(), dateImages);
                }
                if (imageTitle != null) {
                    currentImage.setImageTitle(imageTitle);
                }
                mDataList.add(currentImage); //再加入数据项

                if (dateImages != null) {
                    dateImages.add(currentImage);
                }
            }
            imageInfoList.clear();

            this.updateContentLength(contentLength);
        } else {
            Log.d("Wbj", "没有数据了");
        }

        //mContentAdapter.notifyItemRangeChanged(0, imageInfoList.size());
        mContentAdapter.notifyDataSetChanged();
    }

    private void updateContentLength(int contentLength) {
        ImageInfo itemFooter = ImageInfo.getImageInfo().getItemFooter();
        if (itemFooter == null) {
            ImageInfo.getImageInfo().setItemFooter(new ImageInfo(contentLength, ContentAdapter.VIEW_TYPE_FOOTER));
        } else {
            ImageInfo.getImageInfo().getItemFooter().setContentLength(itemFooter.getContentLength() + contentLength);
        }

        itemFooter = ImageInfo.getImageInfo().getItemFooter();
        mDataList.remove(itemFooter);
        mDataList.add(itemFooter);

        ImageInfo.getImageInfo().setContentLength(itemFooter.getContentLength());
    }

    @Override
    public View getBlurredView() {
        return mRecyclerView;
    }

    private class ContentAdapter extends RecyclerAdapter<ImageInfo, RecyclerAdapter.Holder> {
        public static final int VIEW_TYPE_FOOTER = 1;
        public static final int VIEW_TYPE_TITLE = 2;

        public ContentAdapter(List<ImageInfo> dataList) {
            super(dataList);
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getItemType();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_TITLE) {
                return new TitleHolder(parent);
            } else if (viewType == VIEW_TYPE_FOOTER) {
                return new FooterHolder(parent);
            } else {
                return new ContentHolder(parent);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int pst, ImageInfo data) {
            if (holder instanceof FooterHolder) {
                ((FooterHolder) holder).mTextCount.setText(getString(R.string.picture_bottom_number, data.getContentLength()));
            }

            if ((holder instanceof TitleHolder)) {
                final TitleHolder titleHolder = (TitleHolder) holder;
                titleHolder.mTextDate.setText(data.getTitle());
                titleHolder.mCheckBox.setVisibility(View.GONE);
            }

            if (holder instanceof ContentHolder) {
                ContentHolder contentHolder = (ContentHolder) holder;
                Glide.with(SuhengRecyclerFragment2.this).load(data.getPath()).into(contentHolder.mImageView);
                //Glide.with(SuhengRecyclerFragment2.this).load(R.drawable.beauty2).into(contentHolder.mImageView);
            }
        }
    }

    private final static class ContentHolder extends RecyclerAdapter.Holder {
        ImageView mImageView;

        ContentHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.activity_picture_manager_adt);
            mImageView = itemView.findViewById(R.id.apma_image_view);
        }
    }

    private final static class FooterHolder extends RecyclerAdapter.Holder {
        TextView mTextCount;

        FooterHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.activity_picture_manager_adt_footer);
            mTextCount = itemView.findViewById(R.id.apmaf_text_count);
        }
    }

    private final static class TitleHolder extends RecyclerAdapter.Holder {
        TextView mTextDate;
        CheckBox mCheckBox;

        TitleHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.activity_picture_manager_adt_title);
            mTextDate = itemView.findViewById(R.id.apmat_text_date);
            mCheckBox = itemView.findViewById(R.id.checkbox);
        }
    }

}
