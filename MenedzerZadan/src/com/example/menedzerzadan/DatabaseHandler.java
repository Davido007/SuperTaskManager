package com.example.menedzerzadan;

import java.text.DecimalFormat;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "MENEDZERZADAN.db";
	private static final String TASKS_TABLE_NAME = "TASKS";
	private static final String KEY_ID = "ID";
	private static final String KEY_PRZEDZIALCZASU = "PRZEDZIALCZASU";
	private static final String KEY_LATITUDE = "LATITUDE";
	private static final String KEY_LONGITUDE = "LONGITUDE";
	private static final String KEY_DESCRIPTION = "DESCRIPTION";
	private static final String KEY_ACTION = "ACTION"; //akcja do wykonania przy danym zadaniu

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		//db.execSQL("PRAGMA foreign_keys = ON;");
		
		
		String CREATE_TASKS_TABLE = "CREATE TABLE " + TASKS_TABLE_NAME + "("
				+ KEY_ID + " INTEGER PRIMARY KEY, " + KEY_PRZEDZIALCZASU
				+ " TEXT, " + KEY_LATITUDE + " REAL, " + KEY_LONGITUDE
				+ " REAL, " + KEY_ACTION + " TEXT)";
		db.execSQL(CREATE_TASKS_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	
	public void addPoint(String przedzialCzasu, double latitude, double longitude, String action, String description) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_PRZEDZIALCZASU, przedzialCzasu);
		values.put(KEY_LATITUDE, latitude);
		values.put(KEY_LONGITUDE, longitude);
		values.put(KEY_DESCRIPTION, description);
		values.put(KEY_ACTION, action);
		db.insert(TASKS_TABLE_NAME, null, values);
	}
	
	
	public void deleteAllResults() throws Exception {
		SQLiteDatabase db = null;
		
			db = this.getWritableDatabase();
			//db.execSQL("PRAGMA foreign_keys = ON;");
	    	db.execSQL("DELETE FROM "+TASKS_TABLE_NAME+";");
	    	db.close();
	}
	
	public void updatePrzedzialCzasuZadania(String staryPrzedzialCzasu, String opis, String nowyPrzedzialCzasu) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_PRZEDZIALCZASU, nowyPrzedzialCzasu);
		String whereClause = "PRZEDZIALCZASU='"+staryPrzedzialCzasu+"'"+" AND DESCRIPTION='"+opis+"'";
		db.update(TASKS_TABLE_NAME, values, whereClause, null);
		db.close();
	}
	
	public void updateLatitudeLongitudeZadania(double oldLatitude, double oldLongitude, 
			double newLatitude, double newLongitude, String opis) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_LATITUDE, newLatitude);
		values.put(KEY_LONGITUDE, newLongitude);
		String whereClause = "LATITUDE='"+oldLatitude+"'"+" AND LONGITUDE='"+oldLongitude+"'"+" AND DESCRIPTION='"+opis+"'";
		db.update(TASKS_TABLE_NAME, values, whereClause, null);
		db.close();
	}
	
	
	public void deleteSinglePositionFromSavedTasks(String przedzialCzasu, String opis) throws Exception {
		SQLiteDatabase db = null;
		db = this.getWritableDatabase();
		//db.execSQL("PRAGMA foreign_keys = ON;");
    	db.execSQL("DELETE FROM "+TASKS_TABLE_NAME+" WHERE PRZEDZIALCZASU='"+przedzialCzasu+"' AND DESCRIPTION='"+opis+"';");
		if (db.isOpen()) db.close();
		}
	
	public double[][] getAllTasksCoordinates() {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		double[][] result = null;
		try {
			db = this.getReadableDatabase();
			cursor = db.rawQuery("SELECT LONGITUDE, LATITUDE" + " FROM " + TASKS_TABLE_NAME + " ORDER BY ID ASC", null);
            int longitudeColumnIndex = cursor.getColumnIndexOrThrow("LONGITUDE");
            int latitudeColumnIndex = cursor.getColumnIndexOrThrow("LATITUDE");
            result = new double[cursor.getCount()][2];
			if (cursor.moveToFirst()) {
				do {
					double longitude = cursor.getDouble(longitudeColumnIndex);
					double latitude = cursor.getDouble(latitudeColumnIndex);
					result[cursor.getPosition()][0] = longitude;
					result[cursor.getPosition()][1] = latitude;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		return result;
	}
	
	public double[][] getAllTasksPrzedzialCzasuAndDescription() {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		double[][] result = null;
		try {
			db = this.getReadableDatabase();
			cursor = db.rawQuery("SELECT PRZEDZIALCZASU, DESCRIPTION" + " FROM " + TASKS_TABLE_NAME + " ORDER BY ID ASC", null);
            int przedzialCzasuColumnIndex = cursor.getColumnIndexOrThrow("PRZEDZIALCZASU");
            int descriptionColumnIndex = cursor.getColumnIndexOrThrow("DESCRIPTION");
            result = new double[cursor.getCount()][2];
			if (cursor.moveToFirst()) {
				do {
					double przedzialCzasu = cursor.getDouble(przedzialCzasuColumnIndex);
					double description = cursor.getDouble(descriptionColumnIndex);
					result[cursor.getPosition()][0] = przedzialCzasu;
					result[cursor.getPosition()][1] = description;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		return result;
	}
	
}
