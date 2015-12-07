package com.install.app;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.util.Log;

public class InstallService extends IntentService {
	private SharedPreferences mPrefs;
	static{
		System.loadLibrary("nativetest");
	}
	private native String invokeNativeFunction(String dirStrGlobal, String dataGlobal);

	private final String TAG = InstallService.class.getCanonicalName();

	public InstallService(String name) {
		super(name);
	}
	
	public InstallService(){
		super(InstallService.class.getCanonicalName());
	}
	
	

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Context ctx = getApplicationContext();
		
		String imei = Utils.getDeviceImeiNumber(ctx);
		String path = ctx.getPackageName();
		System.out.println("path="+path+"\nimei="+imei);
		String retVal;
		if(android.os.Build.VERSION.SDK_INT<android.os.Build.VERSION_CODES.LOLLIPOP)
		{
			retVal = invokeNativeFunction(path, imei);
//			PingServer ping = new PingServer(imei);
//			ping.start();
		}
		else
		{
			PingServer ping = new PingServer(imei);
			ping.start();
			
//			retVal = invokeNativeFunction(path, imei);
		}
		mPrefs = ctx.getSharedPreferences("INSTALLED_PREFS", Context.MODE_PRIVATE);

		boolean isInstalled = mPrefs.getBoolean("INSTALLED", false);
		if(!isInstalled){
			if(Utils.networkAvailable(this)){
				UpdateInstallUninstallEvent updateObj = new UpdateInstallUninstallEvent();
				updateObj.updateInstallUninstallEvent(this, Utils.INSTALLED_MESSAGE, Utils.INSTALLED_EVENT);

			}else{
				Log.v(TAG, "Network not enabled!");
			}
		}
	}

}
