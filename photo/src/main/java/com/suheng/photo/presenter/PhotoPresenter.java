package com.suheng.photo.presenter;

import android.text.format.DateUtils;
import android.util.Log;

import com.suheng.photo.PictureAdapter;
import com.suheng.photo.model.ImageInfo;
import com.suheng.photo.model.QueryTask;
import com.suheng.photo.utils.DateUtil;
import com.suheng.photo.view.PhotoView;
import com.suheng.structure.ui.architecture.presenter.BasicPresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoPresenter extends BasicPresenter<PhotoView> implements QueryTask.TaskResultListener {
    private static final int DADA_OBTAIN_BATCH = 40;
    private boolean mHasLoadCompleted;
    private List<ImageInfo> mDataList, mSelectedList;
    private Map<String, List<ImageInfo>> mMapDateImage = new HashMap<>();

    public PhotoPresenter(PhotoView view) {
        super(view);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDataList = Collections.synchronizedList(new ArrayList<ImageInfo>());
        mSelectedList = Collections.synchronizedList(new ArrayList<ImageInfo>());
    }

    public void load() {
        QueryTask queryTask = new QueryTask(getContext());
        queryTask.execute(ImageInfo.getImageInfo().getContentLength(), DADA_OBTAIN_BATCH);
        queryTask.setTaskResultListener(this);
    }

    @Override
    public void onPostExecute(List<ImageInfo> imageInfoList) {
        if ((imageInfoList == null || imageInfoList.size() <= 0)) {
            /*mGridEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);*/
            return;
        }
        /*if (mRecyclerView.getVisibility() != View.VISIBLE) {
            mGridEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }*/

        mHasLoadCompleted = imageInfoList.size() < DADA_OBTAIN_BATCH;
        if (mHasLoadCompleted) {
            getView().showToast("加载完毕！");
        }

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
                if (isNotSameDay) {//如果不是同一天
                    imageTitle = new ImageInfo(currentImage.getDate(), PictureAdapter.VIEW_TYPE_TITLE);
                    mDataList.add(imageTitle);//先加入日期Title项

                    dateImages = new ArrayList<>();
                    mMapDateImage.put(currentImage.getDate(), dateImages);
                }
                if (imageTitle != null) {
                    currentImage.setImageTitle(imageTitle);
                }
                mDataList.add(currentImage);//再加入数据项

                if (dateImages != null) {
                    dateImages.add(currentImage);
                }
            }

            this.updateContentLength(contentLength);
        } else {
            Log.d(mTag, "没有数据了");
        }

        getView().notifyDataSetChanged();
        //mPictureAdapter.notifyDataSetChanged();
    }

    private void updateContentLength(int contentLength) {
        ImageInfo itemFooter = ImageInfo.getImageInfo().getItemFooter();
        if (itemFooter == null) {
            ImageInfo.getImageInfo().setItemFooter(new ImageInfo(contentLength, PictureAdapter.VIEW_TYPE_FOOTER));
        } else {
            ImageInfo.getImageInfo().getItemFooter().setContentLength(itemFooter.getContentLength() + contentLength);
        }

        itemFooter = ImageInfo.getImageInfo().getItemFooter();
        if (mDataList.contains(itemFooter)) {
            mDataList.remove(itemFooter);
        }
        mDataList.add(itemFooter);

        ImageInfo.getImageInfo().setContentLength(itemFooter.getContentLength());
    }

    public List<ImageInfo> getDataList() {
        return mDataList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataList.clear();
        mSelectedList.clear();
        mMapDateImage.clear();
    }
}
