package com.install.app;

import org.json.JSONObject;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

public class InstallUninstallAsyncTask extends AsyncTask<String, Integer, String> {
	private final String TAG = InstallUninstallAsyncTask.class.getCanonicalName();

	private SharedPreferences mPrefs;
	private Context mContext;
	private String mImei;
	private String mInstallEventMsg;
	private String mLatitude;
	private String mLongitude;
	private String mEvent;

	public InstallUninstallAsyncTask(Context ctx, String imei, String msg, String latitude, String longitude, String event){
		this.mContext = ctx;
		this.mImei = imei;
		this.mInstallEventMsg = msg;
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mEvent = event;
	}

	@Override
	protected void onPreExecute(){
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... arg0) {
		String result = null;
		String url = null;

		try{
			if(mEvent.equals(Utils.INSTALLED_EVENT)){
				url = Utils.BASE_URL + Utils.USER_INSTALLED_URL + mImei + "/" + mInstallEventMsg +"/" + mLatitude + "/" + mLongitude;

			}else if(mEvent.equals(Utils.UNINSTALLED_EVENT)){
				url = Utils.BASE_URL + Utils.USER_UNINSTALLED_URL + mImei + "/" + mInstallEventMsg +"/" + mLatitude + "/" + mLongitude;
			}
			if(url!=null &&url.length()>0){
				Log.v(TAG, "URL: "+url);
				JSONParser jParser = new JSONParser();
				result = jParser.getResponseString(url);
				if(result!=null){
					Log.v(TAG, "EVENT RESPONSE: "+result);
				}
				return result;
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return result;
	}


	@Override
	protected void onPostExecute(String result){
		super.onPostExecute(result);
		if(result!=null && result.length()>0){
			mPrefs = mContext.getSharedPreferences("INSTALLED_PREFS", Context.MODE_PRIVATE);
			try{
				JSONObject jObj = new JSONObject(result);
				if(jObj!=null && jObj.length()>0){
					if(jObj.has("status")){
						boolean status = jObj.getBoolean("status");
						if(status){
							Editor editor = mPrefs.edit();
							editor.putBoolean("INSTALLED", true);
							editor.commit();
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}
}
