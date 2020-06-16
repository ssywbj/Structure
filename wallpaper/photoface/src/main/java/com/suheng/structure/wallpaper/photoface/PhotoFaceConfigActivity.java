package com.suheng.structure.wallpaper.photoface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class PhotoFaceConfigActivity extends AppCompatActivity {
    public static final String TAG = PhotoFaceConfigActivity.class.getSimpleName();
    private static final int REQUEST_CODE_TAKE_PHOTO = 11;
    private static final int REQUEST_CODE_PICK_PICTURE = 12;
    private static final int REQUEST_CODE_CLIP = 13;
    public static final String PREFS_FILE = "file_photo_watch_face_config";
    public static final String PREFS_KEY_PATH = "prefs_key_path";
    public static final String ACTION_SET_WATCH_FACE_PHOTO = "action_set_watch_face_photo";
    private SharedPreferences mPrefs;
    private String mPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_face_config);

        mPrefs = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
    }

    public void onClickTakePhoto(View view) {
        try {
            mPhotoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    + File.separator + "Origin" + System.currentTimeMillis() + ".jpg";
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri = this.getFileUri(new File(mPhotoPath));
            Log.d(TAG, "uri = " + uri);
            //指定图片存放位置。指定后，在onActivityResult里得到的data将为null
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
        } catch (Exception e) {
            Log.e(TAG, "open camera exception: " + e.toString());
        }
    }

    public void onClickPickFromAlbum(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_PICTURE);
    }

    public void onCrop(Uri uri) {
        mPhotoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                + File.separator + "Origin" + System.currentTimeMillis() + ".jpg";

        Intent intent = new Intent("com.android.camera.action.CROP");  //调用裁剪
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);//设置宽高比例为1：1
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 700);//设置裁剪图片宽高700x700
        intent.putExtra("outputY", 700);
        intent.putExtra("return-data", false);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);//防止出现黑边框
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri(new File(mPhotoPath)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//设置输出格式
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUEST_CODE_CLIP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "resultCode: " + resultCode);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_TAKE_PHOTO:
            case REQUEST_CODE_PICK_PICTURE:
                try {
                    if (requestCode == REQUEST_CODE_PICK_PICTURE) {
                        if (data == null || data.getData() == null) {
                            return;
                        }
                        String[] projection = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(data.getData(), projection, null, null, null);
                        if (cursor == null) {
                            return;
                        }
                        cursor.moveToFirst();
                        mPhotoPath = cursor.getString(cursor.getColumnIndex(projection[0]));
                        cursor.close();

                        Log.d(TAG, "pick from album, path: " + mPhotoPath);
                        this.sendBroadcast();
                        //this.onCrop(data.getData());
                    } else {
                        this.updateSystemAlbum(mPhotoPath);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "on activity result exception: " + e.toString());
                }
                break;
            default:
                break;
        }
    }

    private synchronized void sendBroadcast() {
        mPrefs.edit().putString(PREFS_KEY_PATH, mPhotoPath).apply();
        mPrefs.notify();
        //sendBroadcast(new Intent(ACTION_SET_WATCH_FACE_PHOTO));
    }

    /**
     * 获取图片uri
     */
    private Uri getFileUri(File file) {
        return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
    }

    /**
     * 拍照后，更新系统图库
     */
    private void updateSystemAlbum(String photoPath) {
        MediaScannerConnection.scanFile(this, new String[]{photoPath}
                , new String[]{"image/jpeg", "image/png"}, null);
    }


}
