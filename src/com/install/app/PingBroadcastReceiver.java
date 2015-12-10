package com.install.app;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class PingBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		System.out.println("Service started...");
		PingController ping = PingController.instance(context);
		ping.startService(false,"TestApp","testsub",60);
	}
	
}
