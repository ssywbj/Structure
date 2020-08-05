package com.suheng.structure.wallpaper.photoface;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class PhotoFaceConfigActivity extends AppCompatActivity {
    public static final String TAG = PhotoFaceConfigActivity.class.getSimpleName();
    private static final String ACTION_CLIP = "com.android.camera.action.CROP";
    private static final int REQUEST_CODE_TAKE_PHOTO = 11;
    private static final int REQUEST_CODE_PICK_PICTURE = 12;
    private static final int REQUEST_CODE_CLIP = 13;
    public static final String PREFS_FILE = "file_photo_watch_face_config";
    public static final String PREFS_KEY_PATH = "prefs_key_path";
    private SharedPreferences mPrefs;
    private String mPhotoPath;
    private DisplayMetrics mMetrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_face_config);

        mPrefs = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        getWindowManager().getDefaultDisplay().getRealMetrics(mMetrics);

        Log.d(TAG, "support clip picture: " + this.isActionSupport(ACTION_CLIP));
        //Log.d(TAG, "support take photo: " + this.isActionSupport(MediaStore.ACTION_IMAGE_CAPTURE));
    }

    public void onClickTakePhoto(View view) {
        PhotoFaceConfigActivityPermissionsDispatcher.requestExternalStoragePermissionWithPermissionCheck(this, view.getId());
    }

    public void onClickPickFromAlbum(View view) {
        PhotoFaceConfigActivityPermissionsDispatcher.requestExternalStoragePermissionWithPermissionCheck(this, view.getId());
    }

    public void onCrop(Uri uri) {
        try {
            Intent intent = new Intent(ACTION_CLIP);//调用系统裁剪
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1.0);//设置裁剪宽高比例
            intent.putExtra("aspectY", 1.0 * mMetrics.heightPixels / mMetrics.widthPixels);
            intent.putExtra("outputX", mMetrics.widthPixels);//设置裁剪宽高以适应屏幕宽高，width：320, height：385
            intent.putExtra("outputY", mMetrics.heightPixels);
            intent.putExtra("return-data", true);
            intent.putExtra("scale", true);
            intent.putExtra("scaleUpIfNeeded", true);//防止出现黑边框
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri(new File(mPhotoPath)));
            //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//设置输出格式
            intent.putExtra("noFaceDetection", true);
            startActivityForResult(intent, REQUEST_CODE_CLIP);

            /*Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1.0 * mMetrics.heightPixels / mMetrics.widthPixels);
            intent.putExtra("outputX", mMetrics.widthPixels);
            intent.putExtra("outputY", mMetrics.heightPixels);
            intent.putExtra("scaleUpIfNeeded", true);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CODE_CLIP);*/
        } catch (Exception e) {
            Log.e(TAG, "open clip exception: " + e.toString());
        }
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
                Log.d(TAG, "take photo, path: " + mPhotoPath);
                /*if (data == null || data.getData() == null) {
                    Log.w(TAG, "take photo, intent or uri is null");
                    return;
                }
                this.updateSystemAlbum(mPhotoPath);
                this.notifyChanged();*/
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
                            this.onCrop(this.getImageContentUri(this, mPhotoPath));
                        }

                        //this.onCrop(data.getData());
                    } else {
                        if (this.getPathFromUri(data.getData())) {
                            Log.d(TAG, "clip picture, path: " + mPhotoPath);
                            this.notifyChanged();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "on activity result exception: " + e.toString());
                }
                break;
            default:
                break;
        }
    }

    private void notifyChanged() {
        synchronized (mPrefs) {
            mPrefs.edit().putString(PREFS_KEY_PATH, mPhotoPath).apply();
            mPrefs.notify();
        }
    }

    /**
     * 获取图片uri
     */
    private Uri getFileUri(File file) {
        return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
    }

    private boolean getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            Log.w(TAG, "clip picture, cursor is null");
            return false;
        }
        cursor.moveToFirst();
        mPhotoPath = cursor.getString(cursor.getColumnIndex(projection[0]));
        cursor.close();
        return true;
    }

    private Uri getImageContentUri(Context context, String filePath) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            return null;
        }
    }

    /**
     * 拍照后，更新系统图库
     */
    private void updateSystemAlbum(String photoPath) {
        MediaScannerConnection.scanFile(this, new String[]{photoPath}
                , new String[]{"image/jpeg", "image/png"}, null);
    }

    private boolean isActionSupport(String action) {
        Intent intent = new Intent(action);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
        return resolveInfoList.size() > 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PhotoFaceConfigActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void requestExternalStoragePermission(int businessId) {
        if (businessId == R.id.text_item_pick_picture) {
            try {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_CODE_PICK_PICTURE);

                /*Intent intent = getPhotoPickIntent();
                startActivityForResult(intent, REQUEST_CODE_PICK_PICTURE);*/
            } catch (Exception e) {
                Log.e(TAG, "open camera exception: " + e.toString());
            }
        } else {
            PhotoFaceConfigActivityPermissionsDispatcher.requestCameraPermissionWithPermissionCheck(this);
        }
    }

    private Intent getPhotoPickIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", mMetrics.widthPixels);
        intent.putExtra("outputY", mMetrics.heightPixels);
        intent.putExtra("return-data", true);
        intent.putExtra("scaleUpIfNeeded", true);
        return intent;
    }

    @OnNeverAskAgain({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void neverAskAgainExternalStoragePermission() {//点击不再询问后执行的方法
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("权限申请").setMessage("在设置—应用管理—" + getString(R.string.app_name)
                + "—权限中开启存储权限，以正常使用应用。");
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("下次", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    public void requestCameraPermission() {
        if (this.isActionSupport(MediaStore.ACTION_IMAGE_CAPTURE)) {
            mPhotoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    + File.separator + "Origin" + System.currentTimeMillis() + ".jpg";
            Log.d(TAG, "path = " + mPhotoPath);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri = this.getFileUri(new File(mPhotoPath));
            //Uri uri = this.createImageUri();
            //File file = new File(mPhotoPath);
            //Uri uri = getImageContentUri(this, mPhotoPath);
            Log.d(TAG, "uri = " + uri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//指定图片存放位置。指定后，在onActivityResult里得到的data将为null
            intent.putExtra("return-data", true);
            //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
        } else {
            Toast.makeText(this, R.string.no_camera_or_app, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    public void neverAskAgainCameraPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("权限申请").setMessage("在设置—应用管理—" + getString(R.string.app_name)
                + "—权限中开启相机权限，以正常使用应用。");
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);//引导用户跳到应用设置界面
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("下次", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
