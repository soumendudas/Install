package com.install.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

public class UninstallIntentActivity extends Activity{
	private final String TAG = UninstallIntentActivity.class.getCanonicalName();

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		unInstallApp();

	}

	private void unInstallApp() {
		if(Utils.networkAvailable(this)){
			UpdateInstallUninstallEvent updateObj = new UpdateInstallUninstallEvent();
			updateObj.updateInstallUninstallEvent(this, Utils.UNINSTALLED_MESSAGE, Utils.UNINSTALLED_EVENT);
		}else{
			Log.v(TAG, "Network not enabled!");
		}

		Uri packageUri = Uri.parse("package:com.install.app");
		Intent uninstallIntent =
				new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
		startActivity(uninstallIntent);
		finish();
	}


}
