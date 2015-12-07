package com.install.app;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class UpdateInstallUninstallEvent {
	private final String TAG = UpdateInstallUninstallEvent.class.getCanonicalName();

	public void updateInstallUninstallEvent(Context context, String msg, String event){
		try {   
			TelephonyManager mngr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
			String imei = mngr.getDeviceId();

			if(event.equals(Utils.INSTALLED_EVENT)){
				InstallUninstallAsyncTask installAsyncObj = new InstallUninstallAsyncTask(context, imei, msg, "0.0", "0.0", event);
				installAsyncObj.execute();
			}else{
				String latitude = "0.0";
				String longitude = "0.0";
				String url = Utils.BASE_URL + Utils.USER_UNINSTALLED_URL + imei + "/" + msg +"/" + latitude + "/" + longitude;
				JSONParser pObj = new JSONParser();
				String result = pObj.getResponseString(url);
				if(result!=null){
					Log.v(TAG, "Uninstall Response: "+result);
				}
			}		

		} catch (Exception e) {   
			Log.e("SendMail", e.getMessage(), e);   
		} 	
	}


}
