package org.muellerpete.pebblebmc.ui;

import org.muellerpete.pebblebmc.pebble.PebbleService;
import org.muellerpete.pebblebmc.xbmc.XBMCService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PreferencesActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SettingsFragment settings = new SettingsFragment();
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, settings).commit();
		Context context = getApplicationContext();
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
