package com.jimcloudy.wifispotter;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Main extends Activity {
	
	Cursor cursor;
	WiFiData wifiData;
	SimpleCursorAdapter adapter;
	ListView listHotspots;

	static final String[] FROM = { WiFiData.C_SSID, WiFiData.C_CAPABILITIES, WiFiData.C_LEVEL };
	static final int[] TO = {R.id.txtSSID, R.id.txtCapabilities, R.id.txtLevel};	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listHotspots = (ListView)findViewById(R.id.listHotspots);
        wifiData = new WiFiData(this);
    }
    
	@Override
	protected void onResume(){
		super.onResume();
		setupList();		
	}
	
   @Override
    protected void onDestroy(){
    	super.onDestroy();
    	wifiData.close();
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void setupList(){
    	cursor = wifiData.getHotSpots();
    	startManagingCursor(cursor);
    	
    	if(cursor != null){
    		
    		cursor.moveToFirst();
    		Log.i("Main",cursor.getString(cursor.getColumnIndex(WiFiData.C_SSID)));
    		
    		adapter = new SimpleCursorAdapter(this,R.layout.row, cursor, FROM, TO);
    	
    		listHotspots.setAdapter(adapter);
	
    	}
    }
}
