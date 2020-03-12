package com.suheng.photo;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.photo.model.ImageInfo;
import com.suheng.photo.presenter.PhotoPresenter;
import com.suheng.photo.view.DividerDecoration;
import com.suheng.photo.view.PhotoView;
import com.suheng.structure.ui.architecture.basic.PresenterActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoActivity extends PresenterActivity<PhotoPresenter> implements PhotoView {
    private static final int NUM_COLUMNS = 4;

    PhotoPresenter mPhotoPresenter = new PhotoPresenter(this);
    private RecyclerView mRecyclerView;
    private PictureAdapter mPictureAdapter;
    private int mLastVisibleItemPosition;
    protected int mItemWidth = 280;
    protected boolean mIsEditMode;
    private boolean mHasLoadCompleted;

    @Override
    public PhotoPresenter getPresenter() {
        return mPhotoPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        this.initRecyclerView();
    }

    @Override
    public void openedExternalStoragePermission(int businessId) {
        mPhotoPresenter.load();
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.main_picture_rv);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, NUM_COLUMNS, GridLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
        mRecyclerView.addItemDecoration(new DividerDecoration(this, true));//设置Item分隔线
        mPictureAdapter = new DateSortAdapter(this, mPhotoPresenter.getDataList());
        mPictureAdapter.setHasStableIds(true);//解决adapter闪烁问题
        mRecyclerView.setAdapter(mPictureAdapter);

        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = ((GridLayoutManager) layoutManager);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    //如果是FooterView或TitleView则占所有列，否则只占自己列
                    return (mPictureAdapter.getItemViewType(position) == PictureAdapter.VIEW_TYPE_FOOTER)
                            || (mPictureAdapter.getItemViewType(position) == PictureAdapter.VIEW_TYPE_TITLE)
                            ? gridLayoutManager.getSpanCount() : 1;
                }
            });
        }

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    mLastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d(mTag, "SCROLL_STATE_IDLE, SCROLL_STATE_IDLE, lastVisibleItemPosition: " + mLastVisibleItemPosition);
                    if (!mIsEditMode && !mHasLoadCompleted && (mLastVisibleItemPosition >= mPictureAdapter.getItemCount() - 1)) {
                        Log.d(mTag, "load , load ,load ");
                        //initQueryTask();
                    }
                }
            }
        });

        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {

                DisplayMetrics realMetrics = new DisplayMetrics();
                WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                if (windowManager != null) {
                    windowManager.getDefaultDisplay().getRealMetrics(realMetrics);
                    mItemWidth = (realMetrics.widthPixels - mRecyclerView.getPaddingLeft() - mRecyclerView.getPaddingRight() -
                            (NUM_COLUMNS - 1) * DividerDecoration.LIST_DIVIDER_DIMEN) / NUM_COLUMNS;
                    Log.i(mTag, " mItemWidth: " + mItemWidth);
                }

                //initQueryTask();
                requestExternalStoragePermission(-1);
            }
        });

        mPictureAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                ImageInfo imageInfo = (ImageInfo) mPictureAdapter.getItem(position);
                if (mIsEditMode) {
                    if (imageInfo.getItemType() == PictureAdapter.VIEW_TYPE_CONTENT) {
                        setSelectedItem(imageInfo);
                    } else if (imageInfo.getItemType() == PictureAdapter.VIEW_TYPE_TITLE) {
                        final String infoTitle = imageInfo.getTitle();
                        /*if (mMapDateImage.containsKey(infoTitle)) {
                            setSelectedTitle(imageInfo);
                        }*/
                    }
                } else {
                    if (imageInfo.getItemType() == PictureAdapter.VIEW_TYPE_CONTENT) {
                        showToast(position + "_click");
                    }
                }
            }
        });

        mPictureAdapter.setOnItemLongClickListener(new RecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position, long id) {
                showToast(position + "_long");
                enterEditMode(position);
                return true;
            }
        });
    }

    private void enterEditMode(int pst) {
        mIsEditMode = true;

        if (pst < 0) {
            mPictureAdapter.notifyDataSetChanged();
        } else {
            //this.setSelectedItem(mDataList.get(pst));
        }
    }

    private void exitEditMode() {
        /*for (ImageInfo imageInfo : mSelectedList) {
            if (imageInfo.getItemType() == PictureAdapter.VIEW_TYPE_CONTENT) {
                Log.d(mTag, "selected item: " + imageInfo);
            }
        }
        mIsEditMode = false;
        mSelectedList.clear();
        mPictureAdapter.notifyDataSetChanged();*/
    }

    private void setSelectedItem(ImageInfo imageInfo) {
        if (imageInfo.getItemType() == PictureAdapter.VIEW_TYPE_FOOTER) {
            return;
        }

        /*if (mSelectedList.contains(imageInfo)) {
            mSelectedList.remove(imageInfo);
        } else {
            mSelectedList.add(imageInfo);
        }

        int dateSelectedCount = 0;
        for (ImageInfo info : mSelectedList) {
            if (imageInfo.getDate().equals(info.getDate())) {
                ++dateSelectedCount;
            }
        }
        ImageInfo imageTitle = imageInfo.getImageTitle();
        if (mMapDateImage.containsKey(imageTitle.getTitle())) {
            if (mMapDateImage.get(imageTitle.getTitle()).size() == dateSelectedCount) {
                mSelectedList.add(imageTitle);
            } else {
                mSelectedList.remove(imageTitle);
            }
        }


        int selectedCount = 0;
        for (ImageInfo info : mSelectedList) {
            if (info.getItemType() == PictureAdapter.VIEW_TYPE_CONTENT) {
                selectedCount++;
            }

        }

        mPictureAdapter.notifyDataSetChanged();*/
    }

    private void setSelectedTitle(ImageInfo imageInfo) {
        if (imageInfo.getItemType() == PictureAdapter.VIEW_TYPE_FOOTER) {
            return;
        }

        /*List<ImageInfo> imageInfos = mMapDateImage.get(imageInfo.getTitle());
        if (mSelectedList.contains(imageInfo)) {
            mSelectedList.remove(imageInfo);

            for (ImageInfo image : imageInfos) {
                mSelectedList.remove(image);
            }
        } else {
            mSelectedList.add(imageInfo);

            for (ImageInfo image : imageInfos) {
                if (!mSelectedList.contains(image)) {
                    mSelectedList.add(image);
                }
            }
        }

        int selectedCount = 0;
        for (ImageInfo info : mSelectedList) {
            if (info.getItemType() == PictureAdapter.VIEW_TYPE_CONTENT) {
                selectedCount++;
            }

        }

        mPictureAdapter.notifyDataSetChanged();*/
    }

    @Override
    public void notifyDataSetChanged() {
        if (mPictureAdapter == null) {
            return;
        }

        mPictureAdapter.notifyDataSetChanged();
    }
}
