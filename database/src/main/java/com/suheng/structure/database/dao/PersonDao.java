package com.suheng.structure.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.suheng.structure.database.bean.Person;
import com.suheng.structure.database.manager.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class PersonDao {
    private DBHelper mDBHelper;

    public PersonDao(Context context) {
        mDBHelper = new DBHelper(context);
    }

    public void insert(Person person) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        String sql = "insert into person(name, age) values(?, ?)";
        database.execSQL(sql, new Object[]{person.getName(), person.getAge()});
        database.close();
    }

    public List<Person> select() {
        List<Person> persons = new ArrayList<>();

        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        String sql = "select * from person";
        Cursor cursor = database.rawQuery(sql, null);
        int id;
        String name;
        int age;
        Person person;
        while (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex("id"));
            name = cursor.getString(cursor.getColumnIndex("name"));
            age = cursor.getInt(cursor.getColumnIndex("age"));
            person = new Person(name, age);
            person.setId(id);
            persons.add(person);
        }
        cursor.close();
        database.close();

        return persons;
    }

    public void delete(int id) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        String sql = "delete from person where id = " + id;
        database.execSQL(sql);
        database.close();
    }

    public void update(String newName, int id) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        String sql = "update person set name = ? where id = ?";
        database.execSQL(sql, new Object[]{newName, id});
        database.close();
    }

    public int count() {
        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        String sql = "select count(*) from person";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        database.close();

        return count;
    }

    public void insertContentValues() {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", "小明");
        values.put("age", 8);
        database.insert("person", null, values);
        values.clear();

        values.put("name", "小明的爸");
        values.put("age", 38);
        database.insert("person", null, values);
        values.clear();

        database.close();
    }

    public void deleteContentValues(int id) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        database.delete("person", "id = ?", new String[]{id + ""});

        database.close();
    }

    public void updateContentValues(int id) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("age", 10);
        database.update("person", values, "id = ?", new String[]{id + ""});
        values.clear();

        database.close();
    }

    public List<Person> selectContentValues() {
        List<Person> persons = new ArrayList<>();

        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        Cursor cursor = database.query("person", null, null, null, null, null, null);
        int id;
        String name;
        int age;
        Person person;
        while (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex("id"));
            name = cursor.getString(cursor.getColumnIndex("name"));
            age = cursor.getInt(cursor.getColumnIndex("age"));
            person = new Person(name, age);
            person.setId(id);
            persons.add(person);
        }
        cursor.close();
        database.close();

        return persons;
    }
}
