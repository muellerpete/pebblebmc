package org.muellerpete.pebblebmc.xbmc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class XBMCCommand {

	public enum ReqId {
		GUI_PROPERTIES, SONG_DETAIL, MOVIE_DETAIL, EPISODE_DETAIL;
	}

	private static JSONObject getBaseObject() {
		JSONObject baseObject = new JSONObject();
		try {
			baseObject.put("jsonrpc", "2.0");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return baseObject;
	}

	public static void sendAction(Context context, String action) {
		JSONObject object = getBaseObject();
		JSONObject parameters = new JSONObject();
		try {
			object.put("method", "Input.ExecuteAction");
			parameters.put("action", action);
			object.put("params", parameters);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i("jsondebug", object.toString());
		Intent intent = new Intent(context, XBMCService.class);
		intent.setAction("org.muellerpete.pebblebmc.xbmc.COMMAND");
		intent.putExtra("json", object.toString());
		context.startService(intent);
	}

	public static void playPause(Context context) {
		sendAction(context, "playpause");
	}

	public static void noop(Context context) {
		sendAction(context, "noop");
	}

	public static void stop(Context context) {
		sendAction(context, "stop");
	}

	public static void stepBack(Context context) {
		sendAction(context, "stepback");
	}

	public static void volumeUp(Context context, int percent) {
		for (int i = 0; i < 10; i++) {
			sendAction(context, "volumeup");
		}
	}

	public static void stepForward(Context context) {
		sendAction(context, "stepforward");
	}

	public static void volumeDown(Context context, int percent) {
		for (int i = 0; i < 10; i++) {
			sendAction(context, "volumedown");
		}
	}

	public static void requestInfo(Context context, int mediaId, String type) {
		Log.d("XBMCCommand", "requestInfo(id = " + mediaId + ", type = " + type
				+ ")");
		JSONObject object = getBaseObject();

		try {
			String library = null;
			String method = null;
			int requestId = -1;
			JSONArray properties = new JSONArray();
			if (type.equals("episode")) {
				library = "VideoLibrary";
				method = "GetEpisodeDetails";
				requestId = ReqId.EPISODE_DETAIL.ordinal();
				properties.put("showtitle");
				properties.put("season");
				properties.put("episode");
				properties.put("originaltitle");
				properties.put("firstaired");
				properties.put("rating");
				properties.put("votes");
				properties.put("productioncode");
			} else if (type.equals("movie")) {
				requestId = ReqId.MOVIE_DETAIL.ordinal();
				library = "VideoLibrary";
				method = "GetMovieDetails";
			} else if (type.equals("song")) {
				requestId = ReqId.SONG_DETAIL.ordinal();
				library = "AudioLibrary";
				method = "GetSongDetails";
			}

			object.put("method", library + "." + method);
			object.put("params", new JSONObject().put(type + "id", mediaId)
					.put("properties", properties));
			object.put("id", requestId);
			Intent intent = new Intent(context, XBMCService.class);
			intent.setAction("org.muellerpete.pebblebmc.xbmc.COMMAND");
			intent.putExtra("json", object.toString());
			context.startService(intent);
		} catch (JSONException e) {
		}

	}

	public static void getVolume(Context context) {
		JSONObject object = getBaseObject();
		JSONObject parameters = new JSONObject();
		try {
			object.put("method", "Application.GetProperties");
			JSONArray items = new JSONArray();
			items.put("volume");
			parameters.put("items", items);
			object.put("params", parameters);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i("jsondebug", object.toString());
		Intent intent = new Intent(context, XBMCService.class);
		intent.setAction("org.muellerpete.pebblebmc.xbmc.COMMAND");
		intent.putExtra("json", object.toString());
		context.startService(intent);
	}

	public static void select(Context context) {
		sendAction(context, "select");
	}

	public static void GUIGetProperties(Context context) {
		JSONObject object = getBaseObject();
		try {
			object.put("method", "GUI.GetProperties");
			JSONObject parameters = new JSONObject();
			JSONArray properties = new JSONArray();
			properties.put("currentwindow");
			properties.put("currentcontrol");
			properties.put("fullscreen");
			parameters.put("properties", properties);
			object.put("params", parameters);
			object.put("id", ReqId.GUI_PROPERTIES.ordinal());
			Log.i("jsondebug", object.toString());
			Intent intent = new Intent(context, XBMCService.class);
			intent.setAction("org.muellerpete.pebblebmc.xbmc.COMMAND");
			intent.putExtra("json", object.toString());
			context.startService(intent);
		} catch (JSONException e) {
		}
	}

	public static void up(Context context) {
		sendAction(context, "up");
	}

	public static void down(Context context) {
		sendAction(context, "down");
	}

	public static void back(Context context) {
		sendAction(context, "back");
	}

	public static void pagedown(Context context) {
		sendAction(context, "pagedown");
	}

	public static void pageup(Context context) {
		sendAction(context, "pageup");
	}

	public static void left(Context context) {
		sendAction(context, "left");
	}

	public static void right(Context context) {
		sendAction(context, "right");
	}

	public static void bigStepForward(Context context) {
		sendAction(context, "bigstepforward");
	}

	public static void bigStepBack(Context context) {
		sendAction(context, "bigstepback");
	}
}
