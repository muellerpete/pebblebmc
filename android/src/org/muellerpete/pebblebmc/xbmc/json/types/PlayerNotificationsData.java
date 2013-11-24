package org.muellerpete.pebblebmc.xbmc.json.types;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

public class PlayerNotificationsData {
	private NotificationsItem notificationsItem;
	private PlayerNotificationsPlayer playerNotificationsPlayer;

	public PlayerNotificationsData(JSONObject data) {
		try {
			notificationsItem = new NotificationsItem(
					data.getJSONObject("item"));
			playerNotificationsPlayer = new PlayerNotificationsPlayer(
					data.getJSONObject("player"));
		} catch (JSONException e) {
			Log.d("jsonDebug", "Error creating PlayerNotificationsData");
			e.printStackTrace();
		}
	}

	public Bundle getExtras() {
		Bundle bundle = new Bundle();
		bundle.putAll(notificationsItem.getExtras());

		bundle.putAll(playerNotificationsPlayer.getExtras());
		return bundle;
	}
}
