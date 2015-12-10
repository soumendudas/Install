package com.install.app;

import android.content.Context;
import android.content.Intent;

public class PingController {
	
	private static final PingController ping = new PingController();
	private static Context context;
	
	public static PingController instance(Context ctx){
		if(context==null)
			context=ctx;
		return ping;
	}
	
	public void startService(boolean flag, String nameStr, String subStr, int pingTime){
		Intent intent = new Intent(context, InstallService.class);
		intent.putExtra("foreground", flag);
		intent.putExtra("notiname", nameStr);
		intent.putExtra("notisub", subStr);
		intent.putExtra("pingtime", pingTime);
		context.startService(intent);
	}

	public void stopService(){
		Intent intent = new Intent(context, InstallService.class);
		context.stopService(intent);
	}
	
}
