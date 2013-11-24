package org.muellerpete.pebblebmc.misc;

import org.muellerpete.pebblebmc.pebble.PebbleService;
import org.muellerpete.pebblebmc.xbmc.XBMCService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction() == "android.system.BOOT_COMPLETED") {
			Intent startXBMCService = new Intent(context, XBMCService.class);
			startXBMCService
					.setAction("org.muellerpete.pebblebmc.xbmc.START_SERVICE");
			context.startService(startXBMCService);
			Intent startPebbleService = new Intent(context, PebbleService.class);
			startPebbleService
					.setAction("org.muellerpete.pebblebmc.pebble.START_SERVICE");
			context.startService(startPebbleService);
		}
	}
}
