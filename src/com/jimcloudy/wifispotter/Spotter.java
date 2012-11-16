package com.jimcloudy.wifispotter;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Spotter extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context,Intent intent){
		String action = intent.getAction();
		Log.i("Spotter",TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		WifiManager wifi;
		wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
			List<ScanResult> connections = wifi.getScanResults();

			Intent i = new Intent(context,LogHotSpot.class);
			i.putParcelableArrayListExtra("connections", (ArrayList<? extends Parcelable>)connections);
			
			context.startService(i);
		}
		if(action.equals(LogHotSpot.LOGGED_INTENT)){
			Log.i("Spotter","stopService");
			context.stopService(new Intent(context,LogHotSpot.class));
		}
		if(action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)){
			Bundle extras = intent.getExtras();
			if(extras != null){
				String state = extras.getString(TelephonyManager.EXTRA_STATE);
				if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
					Intent call = new Intent(context,LogHotSpot.class);
					call.putExtra("phoneCall", true);
					context.startService(call);
				}
			}
		}
			
	}
	
}
