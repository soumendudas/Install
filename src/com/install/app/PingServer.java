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
		
	
	}

}
