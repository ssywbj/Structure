package com.suheng.structure.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.renderscript.Toolkit;
import com.suheng.structure.view.GridItemDecoration;
import com.suheng.structure.view.R;
import com.suheng.structure.view.adapter.RecyclerAdapter;
import com.suheng.structure.view.utils.DateUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PictureManagerActivityBak extends AppCompatActivity {
    private static final int READ_EXTERNAL_STORAGE_CODE = 11;
    private ContentAdapter mContentAdapter;
    private final List<ImageInfo> mDataList = new ArrayList<>();

    private final Rect mRectBlur = new Rect();
    private final Rect mRectBlurred = new Rect();
    private BitmapDrawable mBlurBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_manager);

        RecyclerView recyclerView = findViewById(R.id.view_picture_manager_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        Drawable drawable = AppCompatResources.getDrawable(this, R.drawable.recycler_view_linear_divide_line);
        if (drawable != null) {
            DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            itemDecoration.setDrawable(drawable);
            //recyclerView.addItemDecoration(itemDecoration);
        }
        //final int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        int space = 2;
        GridItemDecoration itemDecoration = new GridItemDecoration(gridLayoutManager, space);
        itemDecoration.setColor(Color.RED);
        recyclerView.addItemDecoration(itemDecoration);
        /*GridItemDecoration4 itemDecoration4 = new GridItemDecoration4(this, gridLayoutManager, space);
        itemDecoration4.setDrawable(drawable);
        recyclerView.addItemDecoration(itemDecoration4);*/

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
                if (adapter == null) {
                    return 1;
                }

                int itemViewType = adapter.getItemViewType(position);
                return ((itemViewType == ContentAdapter.VIEW_TYPE_FOOTER) || (itemViewType == ContentAdapter.VIEW_TYPE_TITLE))
                        ? gridLayoutManager.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mContentAdapter = new ContentAdapter(mDataList));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setTitle("Apply permission")
                        .setMessage("Need read permission")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(PictureManagerActivityBak.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
            }
        } else {
            this.queryPictures();
        }

        ImageView imageSub = findViewById(R.id.fob_image_sub);
        imageSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = loadViewBitmap(recyclerView);
                //Bitmap bitmap = FobRecyclerFrg.loadViewBitmap(recyclerView, mRect);

                /*int[] location1 = new int[2];
                imageSub.getLocationOnScreen(location1);
                int x = location1[0];
                int y = location1[1];
                Log.d("Wbj", "getLocationOnScreen, x: " + x + ", y:" + y);
                bitmap = Bitmap.createBitmap(bitmap, x, y, imageSub.getWidth(), imageSub.getHeight());

                imageSub.setBackground(null);
                imageSub.setBackground(new BitmapDrawable(getResources(), bitmap));*/
            }
        });
        imageSub.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                imageSub.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                int[] location1 = new int[2];
                recyclerView.getLocationOnScreen(location1);
                int x1 = location1[0];
                int y1 = location1[1];
                Log.d("Wbj", "getLocationOnScreen, x: " + x + ", y:" + y + ", x1:" + x1 + ", y1:" + y1);

                Log.d("Wbj", "run: " + imageSub.getMeasuredWidth() + ", " + imageSub.getMeasuredHeight()
                        + "------" + recyclerView.getMeasuredWidth() + ", " + recyclerView.getMeasuredHeight());

                mRectBlur.left = x;
                mRectBlur.top = y;
                mRectBlur.right = mRectBlur.left + imageSub.getMeasuredWidth();
                mRectBlur.bottom = mRectBlur.top + imageSub.getMeasuredHeight();

                mRectBlurred.left = x1;
                mRectBlurred.top = y1;
                mRectBlurred.right = mRectBlurred.left + recyclerView.getMeasuredWidth();
                mRectBlurred.bottom = mRectBlurred.top + recyclerView.getMeasuredHeight();

                boolean intersects = Rect.intersects(mRectBlur, mRectBlurred);
                boolean contains = mRectBlurred.contains(mRectBlur);
                Log.d("Wbj", "rect, intersects: " + intersects + ", contains:" + contains);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                imageSub.post(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap viewBitmap = loadViewBitmap(recyclerView);
                        //Bitmap bitmap = FobRecyclerFrg.loadViewBitmap(recyclerView, mRect);
                        Bitmap bitmap = Bitmap.createBitmap(viewBitmap, Math.abs(mRectBlur.left - mRectBlurred.left), Math.abs(mRectBlur.top - mRectBlurred.top)
                                , imageSub.getWidth(), imageSub.getHeight());

                        Bitmap blurBitmap = Toolkit.INSTANCE.blur(bitmap, 15);
                        if (mBlurBg == null) {
                            mBlurBg = new BitmapDrawable(getResources(), blurBitmap);
                            imageSub.setBackground(mBlurBg);
                            Log.d("Wbj", "run, 1111111111: " + mBlurBg);
                        } else {
                            Log.d("Wbj", "run, blurBitmap: " + mBlurBg.getBitmap());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                Log.d("Wbj", "run, 22222222: " + blurBitmap);
                                mBlurBg.setBitmap(blurBitmap);
                            } else {
                                Bitmap bgBitmap = mBlurBg.getBitmap();
                                if (bgBitmap != null) {
                                    ByteBuffer byteBuffer = ByteBuffer.allocate(blurBitmap.getByteCount());
                                    blurBitmap.copyPixelsToBuffer(byteBuffer);
                                    Log.d("Wbj", "run, 333333333: " + bgBitmap);
                                    bgBitmap.eraseColor(Color.TRANSPARENT);
                                    bgBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(byteBuffer.array()));
                                } /*else {
                                    mBlurBg = new BitmapDrawable(getResources(), blurBitmap);
                                    imageSub.setBackground(mBlurBg);
                                }*/

                                //imageSub.setBackground(new BitmapDrawable(getResources(), blurBitmap));
                            }
                        }
                        //imageSub.setImageBitmap(blurBitmap);
                        if (!bitmap.isRecycled()) {
                            bitmap.recycle();
                        }
                        /*if (!viewBitmap.isRecycled()) {
                            viewBitmap.recycle();
                        }*/
                        //imageSub.setBackground(new BitmapDrawable(getResources(), bitmap));
                    }
                });

            }
        });

        /*ViewTreeObserver viewTreeObserver = imageSub.getViewTreeObserver();
        viewTreeObserver.addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                Log.d("Wbj", "ViewTreeObserver, onDraw, onDraw");
                *//*Bitmap bitmap = FobRecyclerFrg.loadViewBitmap(recyclerView);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, imageSub.getWidth(), imageSub.getHeight());
                //imageSub.setBackground(new BitmapDrawable(getResources(), Toolkit.INSTANCE.blur(bitmap, 15)));
                imageSub.setBackground(new BitmapDrawable(getResources(), bitmap));*//*
            }
        });
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Log.v("Wbj", "ViewTreeObserver, onPreDraw, onPreDraw: " + this);
                *//*Bitmap bitmap = FobRecyclerFrg.loadViewBitmap(recyclerView);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, imageSub.getWidth(), imageSub.getHeight());
                //imageSub.setBackground(new BitmapDrawable(getResources(), Toolkit.INSTANCE.blur(bitmap, 15)));
                imageSub.setBackground(new BitmapDrawable(getResources(), bitmap));*//*
                return true;
            }
        });*/
        /*viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("Wbj", "ViewTreeObserver, onGlobalLayout, onGlobalLayout");
            }
        });*/
        /*viewTreeObserver.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                Log.i("Wbj", "ViewTreeObserver, onScrollChanged, onScrollChanged");
            }
        });*/
    }

    Canvas canvas;
    Bitmap viewBitmap;

    private Bitmap loadViewBitmap(View view) {
        if (viewBitmap == null) {
            viewBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        }
        Log.d("Wbj", "loadViewBitmap, : " + viewBitmap);
        if (canvas == null) {
            canvas = new Canvas();
        }
        viewBitmap.eraseColor(Color.TRANSPARENT);
        viewBitmap.setWidth(view.getWidth());
        viewBitmap.setHeight(view.getHeight());
        canvas.setBitmap(viewBitmap);
        view.draw(canvas);
        return viewBitmap;
    }

    private Bitmap getDownscaledBitmapForView(View view, Rect crop, float downscaleFactor) throws NullPointerException {
        View screenView = view.getRootView();
        //View screenView = view;

        int width = (int) (crop.width() * downscaleFactor);
        int height = (int) (crop.height() * downscaleFactor);

        float dx = -crop.left * downscaleFactor;
        float dy = -crop.top * downscaleFactor;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Matrix matrix = new Matrix();
        matrix.preScale(downscaleFactor, downscaleFactor);
        matrix.postTranslate(dx, dy);
        canvas.setMatrix(matrix);
        screenView.draw(canvas);

        return bitmap;
    }

    @Override
    protected void onDestroy() {
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
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, selectionArgs, sortOrder); //columns传空表示查询所有字段
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
                currentImage.setDate(DateUtils.formatDateTime(this, currentImage.getDateModified(), DateUtils.FORMAT_SHOW_YEAR));
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                this.queryPictures();
            } else {
                Toast.makeText(this, "reject permission", Toast.LENGTH_SHORT).show();
            }
        }
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
                Glide.with(PictureManagerActivityBak.this).load(data.getPath()).into(contentHolder.mImageView);
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
