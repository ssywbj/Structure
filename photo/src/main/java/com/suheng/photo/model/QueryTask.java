package com.suheng.photo.model;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;

import com.suheng.photo.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryTask extends AsyncTask<Integer, Void, List<ImageInfo>> {
    private static final String TAG = QueryTask.class.getSimpleName();
    private Context mContext;

    public QueryTask(Context context) {
        mContext = context;
    }

    @Override
    protected List<ImageInfo> doInBackground(Integer... params) {
        if (params == null || params.length < 2) {
            return null;
        }
        return this.queryPictures(params[0], params[1]);
    }

    @Override
    protected void onPostExecute(List<ImageInfo> imageInfoList) {
        super.onPostExecute(imageInfoList);
        if (mTaskResultListener != null) {
            mTaskResultListener.onPostExecute(imageInfoList);
        }
    }

    private List<ImageInfo> queryPictures(long offset, long count) {
        List<ImageInfo> imageInfoList = new ArrayList<>();
        String columns[] = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.ORIENTATION, MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT, MediaStore.Images.Media.SIZE};//查询字段
        //String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";//时间降序排列
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc limit " + offset + ", " + count;//时间降序排列、分页查询
        /*String absolutePath = "/storage/emulated/0/DCIM/Camera/20161209_120251.jpg";//查询某一张图片
        absolutePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();//查询某个目录下的所有图片
        String selection = MediaStore.Images.Media.DATA + " like ?";//查询条件
        String[] selectionArgs = {absolutePath + "%"};//查询目录*/
        try {
            //Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, selectionArgs, sortOrder);
            Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, sortOrder);
            if (cursor.moveToFirst()) {
                do {
                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    String dirName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    int width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));
                    int height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                    long dateModified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)) * 1000;//*1000:秒转化为毫秒
                    int orientation = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION));
                    ImageInfo imageInfo = new ImageInfo(imagePath, dirName, width, height, size, dateModified, orientation);
                    Log.d(TAG, "queryPictures: " + imageInfo + "\n" + DateUtil.parseYearMonthDay(dateModified) + ", " + new Date().getTime()
                            + "--" + DateUtils.formatDateTime(mContext, dateModified, DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY)
                            + "--" + DateUtils.formatDateTime(mContext, dateModified, DateUtils.FORMAT_SHOW_YEAR)
                            + "--" + DateUtils.formatDateTime(mContext, dateModified, DateUtils.FORMAT_SHOW_DATE));
                    imageInfoList.add(imageInfo);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "queryPictures: " + e.toString(), new Exception());
        }
        return imageInfoList;
    }

    private TaskResultListener mTaskResultListener;

    public void setTaskResultListener(TaskResultListener taskResultListener) {
        mTaskResultListener = taskResultListener;
    }

    public interface TaskResultListener {
        void onPostExecute(List<ImageInfo> imageInfoList);
    }

}