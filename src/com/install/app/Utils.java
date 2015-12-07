package com.install.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class Utils {

	public static final String BASE_URL = "http://203.92.40.134:8080/webApp/rest/ws/";
	public static final String DISCOVER_ALL_DEVICES_URL = "device";
	public static final String USER_UNINSTALLED_URL = "uninstalled/";
	public static final String USER_INSTALLED_URL = "installed/";
	public static final String USER_REGISTRATION_URL = "registration/";

	/**
	 * Constants Used in Application
	 */
	public static final String INSTALLED_EVENT = "install";
	public static final String UNINSTALLED_EVENT = "uninstall";
	public static final String INSTALLED_MESSAGE = "INSTALLED";
	public static final String UNINSTALLED_MESSAGE = "UNINSTALLED";
	public static final String ISALIVE_MESSAGE = "ISALIVE";
	

	public static boolean networkAvailable(Context context) {
		ConnectivityManager connectivityManager
		= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	public static String getVersionName(Context context) throws NameNotFoundException{
		PackageInfo packInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
		return packInfo.versionName;
	}
	
	public static int getVersionID(Context context) throws NameNotFoundException{
		PackageInfo packInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
		return packInfo.versionCode;
	}	
	
	public static String getDeviceImeiNumber(Context context) {
		String imei = getDeviceNumberImei(context);
		if(imei==null || imei.trim().length()==0){
			imei = getDeviceId(context);
		}
		return imei;
	}
	
	private static String getDeviceId(Context context) {
		String imei = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		return imei;
	}
	
	private static String getDeviceNumberImei(Context context){
		TelephonyManager mngr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
		String imei = mngr.getDeviceId();
		return imei;
	}
}
