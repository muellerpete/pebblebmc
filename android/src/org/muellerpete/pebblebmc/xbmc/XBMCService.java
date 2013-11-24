package org.muellerpete.pebblebmc.xbmc;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class XBMCService extends Service {

	public class XBMCServiceLocalBinder extends Binder {
		public XBMCService getXBMCService() {
			return XBMCService.this;
		}

	}

	private static final String TAG = "org.muellerpete.pebblebmc.xbmcservice";
	private final WebSocketConnection connection = new WebSocketConnection();
	private final XBMCServiceLocalBinder binder = new XBMCServiceLocalBinder();
	private SharedPreferences preferences;

	private void connect(final String message) {
		if (!connection.isConnected()) {
			final String wsuri = "ws://"
					+ preferences.getString("pref_xbmc_1_host", "") + ":"
					+ preferences.getString("pref_xbmc_1_port", "9090")
					+ "/jsonrpc";

			try {
				connection.connect(wsuri, new WebSocketHandler() {

					@Override
					public void onOpen() {
						Log.d(TAG, "Status: Connected to " + wsuri);
						connection.sendTextMessage(message);
					}

					@Override
					public void onTextMessage(String payload) {
						if (!payload.isEmpty()) {
							Log.d(TAG, "Got reply: " + payload);
							Intent messageReceivedIntent = new Intent(
									"org.muellerpete.pebblebmc.action.xbmc.MESSAGE");
							messageReceivedIntent.putExtra("json", payload);
							XBMCService.this
									.sendBroadcast(messageReceivedIntent);
						}
					}

					@Override
					public void onClose(int code, String reason) {
						Log.d(TAG, "Connection lost.");
					}
				});
			} catch (WebSocketException e) {

				Log.d(TAG, e.toString());
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		connection.sendTextMessage(intent.getStringExtra("json"));
		return this.binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		XBMCCommand.noop(getApplicationContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flag, int startId) {
		String action = intent.getAction();
		if (action.equals("org.muellerpete.pebblebmc.xbmc.COMMAND")) {
			if (connection.isConnected()) {
				if (intent.hasExtra("json")) {
					Log.d("jsonDebug", intent.getStringExtra("json"));
					connection.sendTextMessage(intent.getStringExtra("json"));
				}
			} else {
				connect(intent.getStringExtra("json"));
			}
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		connection.disconnect();
	}
}
