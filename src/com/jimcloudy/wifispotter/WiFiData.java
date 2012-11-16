package com.jimcloudy.wifispotter;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WiFiData {
	private static final String TAG = WiFiData.class.getSimpleName();
	static final String DATABASE = "WiFiSpotter.db";
	static final int VERSION = 1;
	static final String TABLE = "hotSpots";
	static final String C_ID = "_id";
	static final String C_SSID = "ssid";
	static final String C_CAPABILITIES = "capabilities";	
	static final String C_FREQUENCY = "frequency";
	static final String C_LEVEL = "level";
	static final String C_LAT = "lat";
	static final String C_LONG = "long";

	private static final String GET_ALL_ORDER_BY = C_ID;
	private static final String[] DB_TEXT_COLUMNS = { C_ID, C_SSID, C_CAPABILITIES, C_FREQUENCY, C_LEVEL, C_LAT, C_LONG };

	Context context;

	class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
		    super(context, DATABASE, null, VERSION);
		}

	    @Override
	    public void onCreate(SQLiteDatabase db) {
	      String sql = "CREATE TABLE hotSpots ( _id text primary key , ssid text, capabilities text, frequency int, level int, lat text, long text );";
	      db.execSQL(sql);
	    }

	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	      db.execSQL("drop table " + TABLE);
	      this.onCreate(db);
	    }
	  }

	final DbHelper dbHelper;

	public WiFiData(Context c){
		this.dbHelper = new DbHelper(c);
		context = c;
	}

	public void close(){
		this.dbHelper.close();
	}

	public boolean insertOrIgnore(ContentValues values){
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		long row = -1;
		try{		
			 row = db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		}
		finally{
			db.close();
		}
		if(row<0){
			return true;
		}
		else{
			return false;
		}
	}

	public Cursor getHotSpots() {
	    SQLiteDatabase db = this.dbHelper.getReadableDatabase();
	    try{
	    	Cursor cursor = db.query(TABLE, DB_TEXT_COLUMNS, null, null, null, null, null);
	    	return cursor.moveToFirst() ? cursor : null;
	    }
	    finally{
	    	db.close();
	    }
	  }
	
	public Cursor getHotSpotById(String id) {
	    SQLiteDatabase db = this.dbHelper.getReadableDatabase();
	    String[] selection = {id};
	    try{
	    	Cursor cursor = db.query(TABLE, DB_TEXT_COLUMNS, C_ID + "=?", selection, null, null, null);
	    	return cursor.moveToFirst() ? cursor : null;
	    }
	    finally{
	    	db.close();
	    }
	}

	public boolean updateHotSpots(String hotSpot, ContentValues values) {
	    SQLiteDatabase db = this.dbHelper.getWritableDatabase();
	    Cursor cursor = getHotSpotById(values.getAsString(C_ID));
	    boolean flag;
	    try {
	    	String[] rows = new String[]{hotSpot};
	    	db.update(TABLE, values, C_ID + "=?", rows);
	    	flag = true;
	    } 
	    catch(Exception e){
	    	e.printStackTrace();
	    	flag = false;
	    }
	    finally {
	      db.close();
	    }
	    return flag;
	  }

	public void deleteHotSpots(List<String> hotSpots) {
	    SQLiteDatabase db = this.dbHelper.getWritableDatabase();
	    try {
	    	for(String hotSpot : hotSpots)
	    	{
	    		String[] rows = new String[]{hotSpot};
	    		db.delete(TABLE, C_ID + "=?", rows);
	    	}
	    } 
	    catch(Exception e){
	    	e.printStackTrace();
	    }
	    finally {
	      db.close();
	    }
	  }

	public void delete() {
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
   	    db.delete(TABLE, null, null);
	    db.close();
	  }
}
