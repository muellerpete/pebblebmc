package org.muellerpete.pebblebmc.xbmc.json.types;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

public class PlayerNotificationsPlayer {
	private int playerid = -1;

	public int getPlayerid() {
		return playerid;
	}

	public int getSpeed() {
		return speed;
	}

	private int speed = 0;

	public PlayerNotificationsPlayer(JSONObject object) {
		try {
			playerid = object.getInt("playerid");
			speed = object.getInt("speed");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Bundle getExtras() {
		Bundle bundle = new Bundle();
		bundle.putInt("playerid", playerid);
		bundle.putInt("speed", speed);
		return bundle;
	}

}
