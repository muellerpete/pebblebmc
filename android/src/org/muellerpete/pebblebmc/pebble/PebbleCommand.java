package org.muellerpete.pebblebmc.pebble;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PebbleCommand {

	public static void sendMediaUpdate(Context context, String text, byte row) {
		Log.d("PebbleCommand", "sendMediaUpdate");
		Intent mediaUpdateIntent = new Intent(context, PebbleService.class);
		mediaUpdateIntent
				.setAction("org.muellerpete.pebblebmc.action.pebble.MEDIA_UPDATE");
		mediaUpdateIntent.putExtra("row", row);
		mediaUpdateIntent.putExtra("text", text);
		context.startService(mediaUpdateIntent);
	}

	public static void startPlayer(Context context) {
		Log.d("PebbleCommand", "startPlayer");
		Intent startPlayerIntent = new Intent(context, PebbleService.class);
		startPlayerIntent
				.setAction("org.muellerpete.pebblebmc.action.pebble.START_PLAYER");
		context.startService(startPlayerIntent);
	}

	public static void stopPlayer(Context context) {
		Log.d("PebbleCommand", "startPlayer");
		Intent startPlayerIntent = new Intent(context, PebbleService.class);
		startPlayerIntent
				.setAction("org.muellerpete.pebblebmc.action.pebble.STOP_PLAYER");
		context.startService(startPlayerIntent);
	}
}
