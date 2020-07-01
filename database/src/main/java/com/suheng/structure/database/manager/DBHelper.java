package com.suheng.structure.database.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String NAME = "db_person";
    private static final int VERSION = 1;

    //建表语句（创建用户表）
    public static final String CREATE_PERSON = "create table person ("
            + "id integer primary key autoincrement, "
            + "name text, "
            + "age integer"
            + ")";


    public DBHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PERSON);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
