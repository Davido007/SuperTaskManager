package com.example.menedzerzadan;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "MENEDZERZADAN.db";
	private static final String TASKS_TABLE_NAME = "TASKS";
	private static final String KEY_ID = "ID";
	//private static final String KEY_PRZEDZIALCZASU = "PRZEDZIALCZASU";
	private static final String KEY_STARTDATE = "STARTDATE";
	private static final String KEY_ENDDATE = "ENDDATE";
	private static final String KEY_STARTTIME = "STARTTIME";
	private static final String KEY_ENDTIME = "ENDTIME";
	private static final String KEY_LATITUDE = "LATITUDE";
	private static final String KEY_LONGITUDE = "LONGITUDE";
	private static final String KEY_DESCRIPTION = "DESCRIPTION";
	private static final String KEY_ACTION = "ACTION"; //akcja do wykonania przy danym zadaniu
	private static final String KEY_RADIUS = "RADIUS";
	private static final String KEY_TELEPHONE = "TELEPHONE";
	private static final String KEY_SMSTEXT = "SMSTEXT";
	private static final String KEY_EXECUTED = "EXECUTED";
	

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		//db.execSQL("PRAGMA foreign_keys = ON;");
		
		
		String CREATE_TASKS_TABLE = "CREATE TABLE " + TASKS_TABLE_NAME + "("
				+ KEY_ID + " INTEGER PRIMARY KEY, " 
				+ KEY_STARTDATE + " TEXT, " 
				+ KEY_ENDDATE + " TEXT, "  
				+ KEY_STARTTIME + " TEXT, " 
				+ KEY_ENDTIME + " TEXT, " 
				+ KEY_LATITUDE + " REAL, " 
				+ KEY_LONGITUDE + " REAL, " 
				+ KEY_ACTION + " TEXT, "
				+ KEY_DESCRIPTION + " TEXT, "
				+ KEY_TELEPHONE + " TEXT, "
				+ KEY_SMSTEXT + " TEXT, "
				+ KEY_EXECUTED + " INTEGER DEFAULT 0, "
				+ KEY_RADIUS + " REAL)";
		db.execSQL(CREATE_TASKS_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("DatabaseHandler", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS "+TASKS_TABLE_NAME);
        onCreate(db);
	}

	
	public void addTask(String startDate, String endDate, String startTime, String endTime, 
			double latitude, double longitude, String action, String description, String telephone, String smsText, double radius) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_STARTDATE, startDate);
		values.put(KEY_ENDDATE, endDate);
		values.put(KEY_STARTTIME, startTime);
		values.put(KEY_ENDTIME, endTime);
		values.put(KEY_LATITUDE, latitude);
		values.put(KEY_LONGITUDE, longitude);
		values.put(KEY_ACTION, action);
		values.put(KEY_DESCRIPTION, description);
		values.put(KEY_TELEPHONE, telephone);
		values.put(KEY_SMSTEXT, smsText);
		values.put(KEY_RADIUS, radius);
		db.insert(TASKS_TABLE_NAME, null, values);
	}
	
	public void updateTask(String oldStartDate, String oldEndDate, String oldStartTime, String oldEndTime, 
			double oldLatitude, double oldLongitude, String oldAction, String oldDescription, String oldTelephone, String oldSmsText, double oldRadius, 
			String startDate, String endDate, String startTime, String endTime, 
			double latitude, double longitude, String action, String description, String telephone, String smsText, double radius) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_STARTDATE, startDate);
		values.put(KEY_ENDDATE, endDate);
		values.put(KEY_STARTTIME, startTime);
		values.put(KEY_ENDTIME, endTime);
		values.put(KEY_LATITUDE, latitude);
		values.put(KEY_LONGITUDE, longitude);
		values.put(KEY_ACTION, action);
		values.put(KEY_DESCRIPTION, description);
		values.put(KEY_TELEPHONE, telephone);
		values.put(KEY_SMSTEXT, smsText);
		values.put(KEY_RADIUS, radius);
		
		String whereClause = KEY_STARTTIME+"='"+oldStartTime+"'"+" AND "+KEY_ENDTIME+"='"+oldEndTime+"'"+" AND "+KEY_STARTDATE+"='"
							 +oldStartDate+"'"+" AND "+KEY_DESCRIPTION+"='"+oldDescription+"'";
		db.update(TASKS_TABLE_NAME, values, whereClause, null);
	}
	
	public void deleteAllResults() throws Exception {
		SQLiteDatabase db = null;
		
			db = this.getWritableDatabase();
			//db.execSQL("PRAGMA foreign_keys = ON;");
	    	db.execSQL("DELETE FROM "+TASKS_TABLE_NAME+";");
	    	db.close();
	}
	
	public void updatePrzedzialCzasuZadania(String opis, String oldstartDate, String oldEndDate, String newStartDate, String newEndDate) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_STARTDATE, newStartDate);
		values.put(KEY_ENDDATE, newEndDate);
		String whereClause = KEY_STARTDATE + "='"+oldstartDate+"'"+" AND "+KEY_ENDDATE + "=" + oldEndDate +" AND DESCRIPTION='"+opis+"'";
		db.update(TASKS_TABLE_NAME, values, whereClause, null);
		db.close();
	}
	
	public void updateLatitudeLongitudeZadania(double oldLatitude, double oldLongitude, 
			double newLatitude, double newLongitude, String description) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_LATITUDE, newLatitude);
		values.put(KEY_LONGITUDE, newLongitude);
		String whereClause = "LATITUDE='"+oldLatitude+"'"+" AND LONGITUDE='"+oldLongitude+"'"+" AND DESCRIPTION='"+description+"'";
		db.update(TASKS_TABLE_NAME, values, whereClause, null);
		db.close();
	}
	
	/**
	 * Oznaczenie zadania z danego dnia jako wykonane
	 * @param startTime
	 * @param endTime
	 * @param description
	 */
	public void updateTaskExecuted(String startTime, String endTime, String startDate, String description) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_EXECUTED, 1);
		String whereClause = KEY_STARTTIME+"='"+startTime+"'"+" AND "+KEY_ENDTIME+"='"+endTime+"'"+" AND "+KEY_STARTDATE+"='"
								+startDate+"'"+" AND "+KEY_DESCRIPTION+"='"+description+"'";
		db.update(TASKS_TABLE_NAME, values, whereClause, null);
		db.close();
	}
	
	
	public void deleteSinglePositionFromSavedTasks(String startTime, String endTime, String startDate, String endDate, String opis) {
		SQLiteDatabase db = null;
		db = this.getWritableDatabase();
		//db.execSQL("PRAGMA foreign_keys = ON;");
    	db.execSQL("DELETE FROM "+TASKS_TABLE_NAME+" WHERE STARTTIME='"+startTime+"' AND ENDTIME='"+endTime
    			+"' AND STARTDATE='"+startDate+"' AND ENDDATE='"+endDate+"' AND DESCRIPTION='"+opis+"'");
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
	/**
	 * Zwraca wspolrzedne wszystkich markerow dla podanego dnia w postaci tablicy double[][]. result[][0] - longitude result[][1] - latitude
	 * @param date
	 * @return
	 */
	public double[][] getTasksCoordinatesForDay(String date) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		double[][] result = null;
		try {
			db = this.getReadableDatabase();
			cursor = db.rawQuery("SELECT LONGITUDE, LATITUDE, EXECUTED" + " FROM " + TASKS_TABLE_NAME + " WHERE STARTDATE='" + date + "' AND EXECUTED=1 ORDER BY ID ASC", null);
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
	
	/**
	 * Zwraca wspolrzedne wszystkich markerow dla podanego dnia w postaci tablicy double[][]. result[][0] - longitude result[][1] - latitude
	 * @param date
	 * @return
	 */
	public ArrayList<Double> getTasksRadiiForDay(String date) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		ArrayList<Double> result = new ArrayList<Double>();
		try {
			db = this.getReadableDatabase();
			cursor = db.rawQuery("SELECT RADIUS, EXECUTED" + " FROM " + TASKS_TABLE_NAME + " WHERE STARTDATE='" + date + "' AND EXECUTED=1 ORDER BY ID ASC", null);
            int radiusColumnIndex = cursor.getColumnIndexOrThrow("RADIUS");
			if (cursor.moveToFirst()) {
				do {
					double radius = cursor.getDouble(radiusColumnIndex);
					result.add(radius);
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
	
	/**
	 * Zwraca akcje do wykonania w danym dniu
	 * @param date
	 * @return
	 */
	public ArrayList<String> getActionsForDay(String date) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		ArrayList<String> result = new ArrayList<String>();
		try {
			db = this.getReadableDatabase();
			cursor = db.rawQuery("SELECT ACTION, EXECUTED" + " FROM " + TASKS_TABLE_NAME + " WHERE STARTDATE='" + date + "' AND EXECUTED=1 ORDER BY ID ASC", null);
            int actionColumnIndex = cursor.getColumnIndexOrThrow("ACTION");
			if (cursor.moveToFirst()) {
				do {
					String action = cursor.getString(actionColumnIndex);
					result.add(action);
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
	
	/**
	 * Zwraca numery telefonu w danym dniu
	 * @param date
	 * @return
	 */
	public ArrayList<String> getTelephoneForDay(String date) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		ArrayList<String> result = new ArrayList<String>();
		try {
			db = this.getReadableDatabase();
			cursor = db.rawQuery("SELECT TELEPHONE, EXECUTED" + " FROM " + TASKS_TABLE_NAME + " WHERE STARTDATE='" + date + "' AND EXECUTED=1 ORDER BY ID ASC", null);
            int telephoneColumnIndex = cursor.getColumnIndexOrThrow("TELEPHONE");
			if (cursor.moveToFirst()) {
				do {
					String telephone = cursor.getString(telephoneColumnIndex);
					result.add(telephone);
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
	
	/**
	 * Zwraca tresci smsa w danym dniu
	 * @param date
	 * @return
	 */
	public ArrayList<String> getSMSTextForDay(String date) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		ArrayList<String> result = new ArrayList<String>();
		try {
			db = this.getReadableDatabase();
			cursor = db.rawQuery("SELECT SMSTEXT, EXECUTED" + " FROM " + TASKS_TABLE_NAME + " WHERE STARTDATE='" + date + "' AND EXECUTED=1 ORDER BY ID ASC", null);
            int smsTextColumnIndex = cursor.getColumnIndexOrThrow("SMSTEXT");
			if (cursor.moveToFirst()) {
				do {
					String smsText = cursor.getString(smsTextColumnIndex);
					result.add(smsText);
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
	
	
	
	/**
	 * Zwraca zadania dla danego dnia
	 * @param date
	 * @return
	 */
	public ArrayList<String> getTasksForDay(String date) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		ArrayList<String> result = new ArrayList<String>();
		try {
			db = this.getReadableDatabase();
			cursor = db.rawQuery("SELECT STARTTIME, ENDTIME, DESCRIPTION" + " FROM " + TASKS_TABLE_NAME + " WHERE STARTDATE='"+date+"' ORDER BY ID ASC", null);
            int startTimeColumnIndex = cursor.getColumnIndexOrThrow("STARTTIME");
            int endTimeColumnIndex = cursor.getColumnIndexOrThrow("ENDTIME");
            int descriptionColumnIndex = cursor.getColumnIndexOrThrow("DESCRIPTION");
			if (cursor.moveToFirst()) {
				do {
					String startTime = cursor.getString(startTimeColumnIndex);
					String endTime = cursor.getString(endTimeColumnIndex);
					String description = cursor.getString(descriptionColumnIndex);
					result.add(startTime+"-"+endTime+"             "+description);
					
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
	
	/**
	 * Zwraca dane do wypelnienia okna edycji zadania result[0] - radius 1 - latitude 2 - longitude
	 * @param date
	 * @return
	 */
	public void getTaskData(String date, String description, String startTime, String endTime, double[] result, String action, String telephone, String smsText) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		
		try {
			db = this.getReadableDatabase();
			cursor = db.rawQuery("SELECT STARTTIME, ENDTIME, DESCRIPTION, RADIUS, LATITUDE, LONGITUDE, ACTION, TELEPHONE, SMSTEXT" 
								+ " FROM " + TASKS_TABLE_NAME + " WHERE STARTDATE='"+date+"' AND "+KEY_STARTTIME+"='"+startTime+
								"' AND "+KEY_ENDTIME+"='"+endTime+"' AND "+KEY_DESCRIPTION+"='"+description+"' ORDER BY ID ASC", null);
            int radiusColumnIndex = cursor.getColumnIndexOrThrow(KEY_RADIUS);
            int latitudeColumnIndex = cursor.getColumnIndexOrThrow(KEY_LATITUDE);
            int longitudeColumnIndex = cursor.getColumnIndexOrThrow(KEY_LONGITUDE);
            int actionColumnIndex = cursor.getColumnIndexOrThrow(KEY_ACTION);
            int telephoneColumnIndex = cursor.getColumnIndexOrThrow(KEY_TELEPHONE);
            int smsTextColumnIndex = cursor.getColumnIndexOrThrow(KEY_SMSTEXT);
			if (cursor.moveToFirst()) {
				do {
					double radius = cursor.getDouble(radiusColumnIndex);
					double latitude = cursor.getDouble(latitudeColumnIndex);
					double longitude = cursor.getDouble(longitudeColumnIndex);
					telephone = cursor.getString(telephoneColumnIndex);
					action = cursor.getString(actionColumnIndex);
					smsText = cursor.getString(smsTextColumnIndex);
					result[0] = radius;
					result[1] = latitude;
					result[2] = longitude;
					
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
	}
	
/*	public double[][] getAllTasksPrzedzialCzasuAndDescription() {
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
	}*/
	
}
