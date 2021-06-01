package com.wiz.watch.facephoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.structure.wallpaper.basic.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PhotoFaceConfigFragment extends Fragment {
    public static final String TAG = PhotoFaceConfigFragment.class.getSimpleName();
    private static final String ACTION_CLIP = "com.android.camera.action.CROP";
    private static final int REQUEST_CODE_TAKE_PHOTO = 11;
    private static final int REQUEST_CODE_PICK_PICTURE = 12;
    private static final int REQUEST_CODE_CLIP = 13;
    public static final String PREFS_FILE = "file_photo_watch_face_config";
    public static final String PREFS_KEY_PATH = "prefs_key_path";
    public static final String ACTION_UPDATE_FACE = "com.wiz.watch.facephoto.action.UPDATE_FACE";
    private SharedPreferences mPrefs;
    private final DisplayMetrics mMetrics = new DisplayMetrics();
    private View mViewCover;
    private Activity mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = getContext();
        return LayoutInflater.from(context).inflate(R.layout.fragment_photo_face_config, container, false);
        //return inflater.inflate(R.layout.fragment_photo_face_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewCover = view.findViewById(R.id.cover_view);
        PackageManager packageManager = getContext().getPackageManager();
        final View takePhoto = view.findViewById(R.id.text_item_take_photo);
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            takePhoto.setVisibility(View.VISIBLE);
            takePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCamera();
                }
            });
        } else {
            takePhoto.setVisibility(View.GONE);
        }
        view.findViewById(R.id.text_item_pick_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPicture();
            }
        });

        view.findViewById(R.id.layout_title_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPrefs = mContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mContext.getWindowManager().getDefaultDisplay().getRealMetrics(mMetrics);

        Log.d(TAG, "support clip picture: " + this.isActionSupport(ACTION_CLIP));
        //Log.d(TAG, "support take photo: " + this.isActionSupport(MediaStore.ACTION_IMAGE_CAPTURE));
    }

    public void onCrop(Uri uri) {
        try {
            Intent intent = new Intent(ACTION_CLIP);//调用系统裁剪
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//android7.0设置输出文件的uri
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("aspectX", 1.0);//设置裁剪宽高比例
            intent.putExtra("aspectY", 1.0 * mMetrics.heightPixels / mMetrics.widthPixels);
            intent.putExtra("outputX", mMetrics.widthPixels);//设置裁剪宽高以适应屏幕宽高，width：320, height：385
            intent.putExtra("outputY", mMetrics.heightPixels);
            intent.putExtra("crop", "true");
            intent.putExtra("return-data", false);
            intent.putExtra("scale", true);//支持缩放
            intent.putExtra("scaleUpIfNeeded", true);//防止出现黑边框
            intent.putExtra("noFaceDetection", true);//取消人脸识别
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//设置输出格式
            startActivityForResult(intent, REQUEST_CODE_CLIP);
        } catch (Exception e) {
            Log.e(TAG, "open clip exception: " + e.toString());
        }
    }

    private void onPickPicture() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, REQUEST_CODE_PICK_PICTURE);

            this.setCoverViewVisible();
        } catch (Exception e) {
            Log.e(TAG, "open pick picture exception: " + e.toString());
        }
    }

    private Uri mPhotoUri;
    private String mPhotoPath;

    private void onCamera() {
        if (this.isActionSupport(MediaStore.ACTION_IMAGE_CAPTURE)) {
            mPhotoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    + File.separator + "Origin" + System.currentTimeMillis() + ".jpg";
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mPhotoUri = this.getFileUri(new File(mPhotoPath));
            Log.d(TAG, "path = " + mPhotoPath + ", uri = " + mPhotoUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);//指定图片存放位置。指定后，在onActivityResult里得到的data将为null
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);

            this.setCoverViewVisible();
        }
    }

    private void setCoverViewVisible() {
        mViewCover.postDelayed(new Runnable() {
            @Override
            public void run() {
                mViewCover.setVisibility(View.VISIBLE);
            }
        }, 300);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "resultCode: " + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            mViewCover.setVisibility(View.GONE);
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_TAKE_PHOTO:
                Log.d(TAG, "take photo, path: " + mPhotoPath);
                this.onCrop(mPhotoUri);
                break;
            case REQUEST_CODE_CLIP:
            case REQUEST_CODE_PICK_PICTURE:
                try {
                    if (data == null || data.getData() == null) {
                        Log.w(TAG, "pick or clip picture, intent or uri is null");
                        return;
                    }

                    if (requestCode == REQUEST_CODE_PICK_PICTURE) {//选择照片后再调用系统的裁剪功能
                        Log.d(TAG, "pick from album, uri: " + data.getData());

                        if (this.getPathFromUri(data.getData())) {
                            Log.d(TAG, "pick picture, path: " + mPhotoPath);
                            this.onCrop(FileUtil.getImageContentUri(mContext, mPhotoPath));
                        }
                    } else {
                        if (this.getPathFromUri(data.getData())) {
                            Log.d(TAG, "clip picture, path: " + mPhotoPath);
                            this.notifyChanged();

                            mContext.findViewById(R.id.text_item_pick_picture).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mContext.finish();
                                }
                            }, 128);

                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "on activity result exception: " + e.toString());
                    mViewCover.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    private void notifyChanged() {
        try {
            String suffix = FileUtil.getSuffix(mPhotoPath);
            //Log.d(TAG, "file suffix: " + suffix);
            File dest = new File(mContext.getDir("photo_face", Context.MODE_PRIVATE)
                    , "temp" + "." + suffix);//放在app的data目录下
            File source = new File(mPhotoPath);
            FileUtil.copy(source, dest);
            //Log.d(TAG, "dest file: " + dest);

            mPrefs.edit().putString(PREFS_KEY_PATH, dest.getAbsolutePath()).apply();
            mContext.sendBroadcast(new Intent(ACTION_UPDATE_FACE));

            boolean delete = source.delete();
            //Log.d(TAG, "dest file: " + dest + ", delete: " + delete);
            if (delete) {
                MediaScannerConnection.scanFile(getContext(), new String[]{mPhotoPath}
                        , new String[]{"image/jpeg", "image/png"}, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Uri getFileUri(File file) {
        return FileProvider.getUriForFile(mContext
                , getContext().getPackageName() + ".fileprovider", file);
    }

    private boolean getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            Log.w(TAG, "clip picture, cursor is null");
            return false;
        }
        cursor.moveToFirst();
        mPhotoPath = cursor.getString(cursor.getColumnIndex(projection[0]));
        cursor.close();
        return true;
    }

    private boolean isActionSupport(String action) {
        Intent intent = new Intent(action);
        PackageManager pm = mContext.getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
        return resolveInfoList.size() > 0;
    }

}
