/**
 * 
 */
package com.game.minesweeper.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Himanshu
 * 
 */
public class MinesweeperData extends SQLiteOpenHelper {

	private static final String TAG = "MinesweeperData";

	private static final String DATABASE_NAME = "minesweeper.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME_USERNAME = "userid";
	private static final String _ID = "userid";

	public MinesweeperData(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "insode onCreate..");
		db.execSQL("CREATE TABLE " + TABLE_NAME_USERNAME + " (" + _ID
				+ "TEXT PRIMARY KEY );");
	}

	public void AddUserId(String userId, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put(_ID + "TEXT", userId);
		db.insertOrThrow(TABLE_NAME_USERNAME, null, values);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	

	public boolean doesUserIdPresent(String username) {
		String[] FROM = { _ID + "TEXT" };
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME_USERNAME, FROM, _ID + "TEXT='"
				+ username + "'", null, null, null, null);
		String userid = "";
		while (cursor.moveToNext()) {
			Log.d(TAG, cursor.getString(0));
			userid  = cursor.getString(0);
		}
		if (null != userid && userid != "")
			return true;
		else
			return false;
	}
}
