package org.muellerpete.pebblebmc.misc;

import org.muellerpete.pebblebmc.xbmc.XBMCService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {

	private boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("NetworkChangeReceiver", "Connectivity changed");
		if (isConnected(context)) {
			Intent startXBMCService = new Intent(context, XBMCService.class);
			startXBMCService
					.setAction("org.muellerpete.pebblebmc.xbmc.RECONNECT");
			context.startService(startXBMCService);
		}
	}
}
