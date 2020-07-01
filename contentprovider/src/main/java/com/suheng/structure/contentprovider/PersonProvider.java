package com.suheng.structure.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.suheng.structure.database.manager.DBHelper;

public class PersonProvider extends ContentProvider {

    private final static String AUTHORITY = "com.suheng.structure.provider";
    private final static int STUDENT_URI_CODE = 0;

    private Context mContext;
    private DBHelper mDBHelper;
    private final static UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "person", STUDENT_URI_CODE);
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        mDBHelper = new DBHelper(mContext);
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int uriType = sUriMatcher.match(uri);
        Log.d("Wbj", "query uri: " + uri + ", type: " + uriType);

        Cursor cursor = null;
        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        if (uriType == STUDENT_URI_CODE) {//query(String table, String[] columns, String selection,String[] selectionArgs, String groupBy, String having,String orderBy, String limit)
            cursor = database.query("person", projection, selection, selectionArgs, null, null, sortOrder, null);
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long row;
        int uriType = sUriMatcher.match(uri);
        Log.d("Wbj", "insert uri: " + uri + ", type: " + uriType);

        if (uriType == STUDENT_URI_CODE) {
            SQLiteDatabase database = mDBHelper.getWritableDatabase();
            row = database.insert("person", null, values);
        } else {
            return null;
        }

        if (row > -1) {
            mContext.getContentResolver().notifyChange(uri, null);
            Log.d("Wbj", "insert row: " + row);
            return ContentUris.withAppendedId(uri, row);
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowDelete;
        int uriType = sUriMatcher.match(uri);
        Log.d("Wbj", "delete uri: " + uri + ", type: " + uriType);

        if (uriType == STUDENT_URI_CODE) {
            SQLiteDatabase database = mDBHelper.getWritableDatabase();
            rowDelete = database.delete("person", selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("UnSupport Uri : " + uri);
        }

        if (rowDelete > 0) {
            Log.d("Wbj", "delete row: " + rowDelete);
            mContext.getContentResolver().notifyChange(uri, null);
        }

        return rowDelete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowUpdate;
        int uriType = sUriMatcher.match(uri);
        Log.d("Wbj", "update uri: " + uri + ", type: " + uriType);

        if (uriType == STUDENT_URI_CODE) {
            SQLiteDatabase database = mDBHelper.getWritableDatabase();
            rowUpdate = database.update("person", values, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("UnSupport Uri : " + uri);
        }

        if (rowUpdate > 0) {
            Log.d("Wbj", "update row: " + rowUpdate);
            mContext.getContentResolver().notifyChange(uri, null);
        }

        return rowUpdate;
    }

}
