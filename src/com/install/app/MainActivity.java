package com.install.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends Activity {
	private SharedPreferences mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		mPrefs = getSharedPreferences("INSTALLED_PREFS", Context.MODE_PRIVATE);

		boolean isInstalled = mPrefs.getBoolean("INSTALLED", false);
		if(!isInstalled){
			System.out.println("about to start broadcast receiver!!!!");
			/*Intent intent = new Intent(this, PingBroadcastReceiver.class);
			startService(intent);*/
			
			Context context = getApplicationContext();
			
			System.out.println("Application context is "+(context==null));
			
			PingController ping = PingController.instance(context);
			ping.startService(true,"TestApp","testsub",60);
		}
		
		/*boolean isRegistered = mPrefs.getBoolean("REGISTERED", false);
		if(!isRegistered){
			startActivity(new Intent(this, RegistrationActivity.class));
		}*/


	}
}
