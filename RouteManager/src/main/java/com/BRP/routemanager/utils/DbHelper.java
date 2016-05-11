package com.BRP.routemanager.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;

import android.util.Log;

import com.BRP.routemanager.R;
import com.BRP.routemanager.app.rmApp;

public class DbHelper extends SQLiteOpenHelper
{
    private Context mContext;
    private SQLiteDatabase db;

    public static final int DATABASE_VERSION = 1;
    public static String DATABASE_DIR = rmApp.getAppContext().getString(R.string.DirName) + "/";
    public static String DATABASE_PATH = rmApp.getAppContext().getFilesDir().getAbsolutePath() + "/" + DATABASE_DIR;
    public static String DATABASE_NAME;
    public static String TABLE_NAME;

    public static final String S_NO = "StopNumber";
    public static final String STOP_NAME = "StopName";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String TIME_STAMP = "Time";
    public static final String [] COLUMNS = {S_NO, STOP_NAME, LATITUDE, LONGITUDE, TIME_STAMP};

    public DbHelper(Context context, String DbName, String TableName) {
	super(context,DATABASE_PATH + DbName, null, DATABASE_VERSION);
	try {
		DATABASE_NAME = DbName;
		TABLE_NAME = TableName;
		mContext = context;

		File dir = new File(DATABASE_PATH);
		dir.mkdirs();

		db = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null,
			SQLiteDatabase.OPEN_READWRITE |
			SQLiteDatabase.CREATE_IF_NECESSARY );

		createTable();
	}
	catch(SQLiteException e) { 
		Log.e("TAG","ERROR",e);
		Toast.makeText(mContext,"SQL Error!", Toast.LENGTH_SHORT).show(); }

    }

    public DbHelper(Context context, String DbName) {
	super(context,DATABASE_PATH + DbName, null, DATABASE_VERSION);
	try {
		mContext = context;
		DATABASE_NAME = DbName;
		TABLE_NAME = "";

		File dir = new File(DATABASE_PATH);
		dir.mkdirs();

		db = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null,
			SQLiteDatabase.OPEN_READWRITE |
			SQLiteDatabase.CREATE_IF_NECESSARY );
	}
	catch(SQLiteException e) { 
		Log.e("TAG","ERROR",e);
		Toast.makeText(mContext,"SQL Error!", Toast.LENGTH_SHORT).show(); }

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void createTable() {
	String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " +
		TABLE_NAME + " ( " +
		"StopNumber INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"StopName TEXT, " +
		"Latitude TEXT NOT NULL, " +
		"Longitude TEXT NOT NULL, " +
        "Time TEXT NOT NULL"+
		")";
	
	db.execSQL(SQL_CREATE_ENTRIES);
    } 

    public void setTable (ArrayList<String> stop, ArrayList<String> lat, ArrayList<String> lon, int start, int size, ArrayList<String> time) {
	delTable();
	createTable();

	if (stop.size() != size || lat.size() != size || lon.size() != size) {
		Toast.makeText(mContext, "Incomplete Data!", Toast.LENGTH_SHORT).show();
		return;
	}
	for (int i = start; i < size; i++ ) {	
		addStop(stop.get(i), lat.get(i), lon.get(i),time.get(i));
	}
    }

    public void addStop(String stopName, String lat, String lon, String time) {
	try {	
		ContentValues values = new ContentValues();
		values.put(STOP_NAME, stopName);
		values.put(LATITUDE, lat);
		values.put(LONGITUDE, lon);
        values.put(TIME_STAMP, time);
	
 		db.insertOrThrow(TABLE_NAME, null, values);
	}
	catch(SQLiteException e) { 
		Log.e("TAG","ERROR",e);
		Toast.makeText(mContext,"SQL Error!", Toast.LENGTH_SHORT).show(); }
    }

    public String getDbPath() {
	return db.getPath();
    }

    public Cursor getTables() {
	Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
	return c;
    }

    public Cursor showTable() {
	String query = "SELECT * FROM " + TABLE_NAME;
	Cursor cursor =	db.rawQuery(query,null);
	return cursor;
    } 

    public String getLastStop() {
	Cursor cursor = showTable();
	if (cursor != null && cursor.getCount()>0 ) {
		cursor.moveToLast();
		return cursor.getString(1);
	}
	return "";
    }

    public void updateTableName(String name) {
	if (name.equals(TABLE_NAME)) {
		return;
	}
	String alter = "ALTER TABLE " + TABLE_NAME + " RENAME TO " + name;
	try {
		db.execSQL(alter);
		TABLE_NAME = name;
	}
	catch (SQLiteException e) {
		Log.e("TAG","Error",e);
		Toast.makeText(mContext,"SQL Error!",Toast.LENGTH_SHORT).show();
	}
    }

    public void updateStops(ArrayList<String> stopList) {
	for (int i = 1; i < stopList.size(); i++ ) {
		ContentValues value = new ContentValues();
		value.put(STOP_NAME, stopList.get(i));
	
		db.update(TABLE_NAME, value, S_NO + " = " + i, null);
	}
    }

    public void delTable() {
	try {
		String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(SQL_DELETE_TABLE);
	}
	catch (SQLiteException e) {
		Toast.makeText(mContext,"SQL Error!", Toast.LENGTH_SHORT).show(); }
    }
	

    public void closeDB() {
	if (db != null) {
		db.close();
	}
    }
}
    