package com.suheng.structure.contentprovider;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.suheng.structure.database.bean.Person;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private final static String AUTHORITY = "com.suheng.structure.provider";
    private final static Uri PERSON_URI = Uri.parse("content://" + AUTHORITY + "/person");
    private static final int MSG_URI_CHANGE = 11;
    private ContentObserver mContentObserver;
    private Handler mHandler = new ContentHandler(this);

    private static class ContentHandler extends Handler {
        private WeakReference<MainActivity> mReference;

        public ContentHandler(MainActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            MainActivity activity = mReference.get();
            if (activity == null) {
                return;
            }
            Log.d("Wbj", "msg: " + msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContentObserver = new ContentObserver(/*new Handler()*/mHandler) {//若Handler为空，那么回调在子线程中调用；若不为空，则回调在主线程中调用，也可以直接new一个Handler进去
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                Log.d("Wbj", "onChange, uri: " + uri + ", selfChange: " + selfChange + ", thread: " + Thread.currentThread().getName());
                mHandler.obtainMessage(MSG_URI_CHANGE).sendToTarget();
            }
        };
        getContentResolver().registerContentObserver(PERSON_URI, true, mContentObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mContentObserver);
    }

    public void onClickInsert(View view) {
        ContentValues values = new ContentValues();
        values.put("name", "巴马");
        values.put("age", 54);
        Uri uri = getContentResolver().insert(PERSON_URI, values);
        Log.d("Wbj", "insert, result uri: " + uri);

        values.clear();
        values.put("name", "小泽");
        values.put("age", 20);
        uri = getContentResolver().insert(PERSON_URI, values);
        Log.d("Wbj", "insert, result uri: " + uri);
    }

    public void onClickDelete(View view) {
        int delete = getContentResolver().delete(PERSON_URI, "id = ?", new String[]{5 + ""});
        Log.d("Wbj", "delete, result: " + delete);
    }

    public void onClickModify(View view) {
        ContentValues values = new ContentValues();
        values.put("age", 90);
        int update = getContentResolver().update(PERSON_URI, values, "id = ?", new String[]{4 + ""});
        Log.d("Wbj", "update, result: " + update);
    }

    public void onClickQuery(View view) {
        Cursor cursor = getContentResolver().query(PERSON_URI, null, null, null, null);
        if (cursor == null) {
            Log.w("Wbj", "cursor is null");
            return;
        }
        Log.d("Wbj", "count: " + cursor.getCount());

        Person person;
        int id, age;
        String name;
        while (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex("id"));
            name = cursor.getString(cursor.getColumnIndex("name"));
            age = cursor.getInt(cursor.getColumnIndex("age"));
            person = new Person(name, age);
            person.setId(id);
            Log.d("Wbj", "person-->" + person);
        }

        cursor.close();
    }
}
