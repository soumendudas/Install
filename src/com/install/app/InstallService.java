package com.install.app;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

//public class InstallService extends IntentService {
public class InstallService extends Service {
	private SharedPreferences mPrefs;
	private boolean foregroundFlag;

	static{
		System.loadLibrary("checkinstall");
	}
	private native String invokeNativeFunction(String dirStrGlobal, String dataGlobal, int sdk_version, int lollipop_ver, int pingTime);

	private final String TAG = InstallService.class.getCanonicalName();

	/*	
	 * to be uncommented if IntentService is used
	 * public InstallService(String name) {
		super(name);
	}

	public InstallService(){
		super(InstallService.class.getCanonicalName());
	}*/



	/*
	 * to be uncommented if IntentService is used
	 * @Override
	protected void onHandleIntent(Intent intent) {

		Context ctx = getApplicationContext();

		String imei = Utils.getDeviceImeiNumber(ctx);
		String path = ctx.getPackageName();
		System.out.println("path="+path+"\nimei="+imei);
		String retVal="";

		PingServer ping = new PingServer(imei);
		ping.start();

		retVal = invokeNativeFunction(path, imei, android.os.Build.VERSION.SDK_INT, android.os.Build.VERSION_CODES.LOLLIPOP);

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
	 */	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);
		Context ctx = getApplicationContext();
		Bundle bundle = intent.getExtras();
		boolean isFore = false;
		
		String appName = "";
		String appSub = "";
		int pingTime = 0;
		if(bundle!=null){
			boolean flag = bundle.containsKey("foreground");
			if(flag){
				isFore = intent.getExtras().getBoolean("foreground");
			}
			flag = bundle.containsKey("notiname");
			if(flag){
				appName = intent.getExtras().getString("notiname");
			}
			flag = bundle.containsKey("notisub");
			if(flag){
				appSub = intent.getExtras().getString("notisub");
			}
			flag = bundle.containsKey("pingtime");
			if(flag){
				pingTime = intent.getExtras().getInt("pingtime");
			}
		}

		if(android.os.Build.VERSION.SDK_INT<android.os.Build.VERSION_CODES.LOLLIPOP){
			callOnReboot(intent, flags, startId, ctx, pingTime);
			return START_REDELIVER_INTENT;
		}

		
		Intent intent2 = new Intent(this, InstallService.class);
		intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent2, 0);

		Notification noti = new Notification.Builder(getApplicationContext())
				.setContentTitle(appName)
				.setContentText(appSub)
				.setSmallIcon(R.drawable.ic_launcher)      
				.build();
		startForeground(1234, noti);       
		callOnReboot(intent, flags, startId, ctx, pingTime);


		if(!isFore){
			return START_REDELIVER_INTENT;
		}
		else{
			return Service.START_STICKY;
		}

	}

	private void callOnReboot(Intent intent, int flags, int startId , Context ctx, int pingTime){


		String imei = Utils.getDeviceImeiNumber(ctx);
		String path = ctx.getPackageName();
		System.out.println("path="+path+"\nimei="+imei);
		String retVal="";

		/*		PingServer ping = new PingServer(imei);
		ping.start();
		 */		
		retVal = invokeNativeFunction(path, imei, android.os.Build.VERSION.SDK_INT, android.os.Build.VERSION_CODES.LOLLIPOP, pingTime);

		/*if(android.os.Build.VERSION.SDK_INT<android.os.Build.VERSION_CODES.LOLLIPOP)
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
		}*/
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
