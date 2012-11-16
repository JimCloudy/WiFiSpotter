package com.jimcloudy.wifispotter;

import java.util.ArrayList;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LogHotSpot extends Service {
	
	WiFiData wifiData;
	ArrayList<ScanResult> connections;
	boolean phoneCall;
	private Logger logger;
	public static final String LOGGED_INTENT = "com.jimcloudy.android.intent.action.LOGGED";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		logger = new Logger();
		phoneCall = false;
		wifiData = new WiFiData(this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flag, int startId) {
		super.onStartCommand(intent, flag, startId);
		Bundle extras = intent.getExtras();
		connections = extras.getParcelableArrayList("connections");
		phoneCall = extras.getBoolean("phoneCall");
		Log.i("LogHotSpot","onStartCommand");
		logger.start();
	    return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		logger.interrupt();
		logger = null;
	}	
	
	private class Logger extends Thread {
		  
	    public Logger() {
	      super("UpdaterService-Logger");
	    }

	    @Override
	    public void run() {
	    	if(connections!=null){
	    		for(ScanResult connection : connections){
	    			ContentValues values = new ContentValues();
	    			values.put(WiFiData.C_ID,connection.BSSID);
	    			values.put(WiFiData.C_SSID, connection.SSID);
	    			values.put(WiFiData.C_CAPABILITIES, connection.capabilities);
	    			values.put(WiFiData.C_FREQUENCY, connection.frequency);
	    			values.put(WiFiData.C_LEVEL, connection.level);
	    			if(!wifiData.insertOrIgnore(values)){
	    				
	    				wifiData.updateHotSpots(connection.BSSID, values);
	    			}
	    		}
	    	}
	    	if(phoneCall){
	    		ContentValues values = new ContentValues();
	    		values.put(WiFiData.C_ID,"01:02:03:04:05:06");
	    		values.put(WiFiData.C_SSID, "thePostMaster");
	    		values.put(WiFiData.C_CAPABILITIES, "[WPA2-PSK-CCMP]");
	    		values.put(WiFiData.C_FREQUENCY, 2422);
	    		values.put(WiFiData.C_LEVEL, -45);
	    		wifiData.insertOrIgnore(values);
	    	}
	    	Intent broadcast = new Intent();
	    	broadcast.setAction(LOGGED_INTENT);
	    	getApplicationContext().sendBroadcast(broadcast);
	    }
	}

}
