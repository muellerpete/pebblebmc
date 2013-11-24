package org.muellerpete.pebblebmc.xbmc.json.types;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

public class NotificationsItem {

	private String type = "unknown";
	private int id = -1;

	public String getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public NotificationsItem(JSONObject object) {
		try {
			type = object.getString("type");
		} catch (JSONException e1) {
			Log.d("jsonDebug", "no type");
		}
		try {
			id = object.getInt("id");
		} catch (JSONException e1) {
			Log.d("jsonDebug", "no id");
		}
	}

	public Bundle getExtras() {
		Bundle bundle = new Bundle();
		bundle.putString("type", type);
		bundle.putInt("id", id);
		return bundle;
	}
}
