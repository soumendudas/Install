package com.install.app;

import android.util.Log;

public class PingServer extends Thread {
	private final String TAG = PingServer.class.getCanonicalName();
	private final int PINGTIME = 10000 *6 ;//10000*6*60*24;
	
	private String imei;
	
	public PingServer(String imei) {
		this.imei=imei;
	}
	
	@Override
	public void run(){
		int count=0; //this is a dummy termination count; to be removed later
		String url = null;
		String result = null;
		
		while(true){
			url = Utils.BASE_URL + Utils.USER_UNINSTALLED_URL + imei + "/" + Utils.ISALIVE_MESSAGE +"/" + "0.0" + "/" + "0.0";
			System.out.println(url);
			if(url!=null &&url.length()>0){
				Log.v(TAG, "URL: "+url);
				JSONParser jParser = new JSONParser();
				result = jParser.getResponseString(url);
				if(result!=null){
					Log.v(TAG, "EVENT RESPONSE: "+result);
				}
			}
			try {
				Thread.sleep(PINGTIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*if(++count==2)
				break;*/
			
		}
	}

}
