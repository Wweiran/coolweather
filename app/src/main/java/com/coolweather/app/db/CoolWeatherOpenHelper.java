package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by WANGWEIRAN on 2016/10/19.
 */

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {


    /**
     * 下面分别是三个表格的建表语句
     * Province表的建表语句
     */
    public static final String CREATE_PROVINCE = "create table Province (" + "id integer primary key autoincrement, "
            + "province_name text, " + "province_code text)";

    /**
     * City表的建表
     */
    public static final String CREATE_CITY = "create table City (" + "id integer primary key autoincrement, "
            + "city_name text, " + "city_code text, " + "province_id integer)";
    /**
     * County表的建表
     */
    public static final String CREATE_COUNTY = "create table County (" + "id integer primary key autoincrement, "
            + "county_name text, " + "county_code text, " + "city_id integer)";

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
