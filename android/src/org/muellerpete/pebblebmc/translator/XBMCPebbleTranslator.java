package org.muellerpete.pebblebmc.translator;

import org.json.JSONException;
import org.json.JSONObject;
import org.muellerpete.pebblebmc.pebble.Constants;
import org.muellerpete.pebblebmc.pebble.PebbleCommand;
import org.muellerpete.pebblebmc.pebble.PebbleService;
import org.muellerpete.pebblebmc.xbmc.XBMCCommand;
import org.muellerpete.pebblebmc.xbmc.XBMCCommand.ReqId;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class XBMCPebbleTranslator extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.hasExtra("json")) {
			String jsonMessage = intent.getStringExtra("json");

			try {
				JSONObject jsonObject = new JSONObject(jsonMessage);
				parseJSON(context, jsonObject);
			} catch (JSONException e) {
				Log.i("jsonDebug", "Malformed JSON");
			}

		}
	}

	private void parseJSON(Context context, JSONObject jsonObject) {
		if (jsonObject.has("method")) {
			parseMethodJSON(context, jsonObject);
		} else if (jsonObject.has("result")) {
			parseResultJSON(context, jsonObject);
		}

	}

	private void parseMethodJSON(Context context, JSONObject jsonObject) {
		String method = jsonObject.optString("method");
		Log.d("STRINGmethod", method);
		String namespace = method.split("\\.")[0];
		method = method.split("\\.")[1];
		if (namespace.equals("Application")) {
			parseApplicationJSON(context, method, jsonObject);
		} else if (namespace.equals("AudioLibrary")) {
			parseAudioLibraryJSON(context, method, jsonObject);
		} else if (namespace.equals("GUI")) {
			parseGUIJSON(context, method, jsonObject);
		} else if (namespace.equals("Input")) {
			parseInputJSON(context, method, jsonObject);
		} else if (namespace.equals("Player")) {
			parsePlayerJSON(context, method, jsonObject);
		} else if (namespace.equals("Playlist")) {
			parsePlaylistJSON(context, method, jsonObject);
		} else if (namespace.equals("System")) {
			parseSystemJSON(context, method, jsonObject);
		} else if (namespace.equals("VideoLibrary")) {
			parseVideoLibraryJSON(context, method, jsonObject);
		} else if (namespace.equals("XBMC")) {
			parseXBMCJSON(context, method, jsonObject);
		} else {
			Log.d("jsonDebug", "unknown namespace: " + jsonObject.toString());
		}
	}

	private void parseResultJSON(Context context, JSONObject jsonObject) {
		try {
			JSONObject result = jsonObject.getJSONObject("result");
			int requestId = jsonObject.getInt("id");
			if (requestId == ReqId.GUI_PROPERTIES.ordinal()) {
				parseGUIPropertiesJSON(context, result);
			} else if (requestId == ReqId.EPISODE_DETAIL.ordinal()) {
				parseEpisodeDetailsJSON(context,
						result.optJSONObject("episodedetails"));
			}
		} catch (JSONException e) {
		}
	}

	private void parseGUIPropertiesJSON(Context context, JSONObject result) {
		try {
			// int windowId =
			// result.getJSONObject("currentwindow").getInt("id");
			boolean isFullscreen = result.getBoolean("fullscreen");
			if (isFullscreen) {
				PebbleCommand.startPlayer(context);
			}
		} catch (JSONException e) {
		}
	}

	private void parseEpisodeDetailsJSON(Context context,
			JSONObject episodeDetails) {
		Log.d("videoLibrary", "parsing episode details");
		String row1 = "";
		String row2 = "";
		String row3 = "";
		String row4 = "";
		if (episodeDetails.has("season")) {
			try {
				row1 = "Season " + episodeDetails.getInt("season");
			} catch (JSONException e) {
			}
		}
		if (episodeDetails.has("episode")) {
			if (!row1.isEmpty()) {
				row1 += "  ";
			}
			try {
				row1 += "Episode " + episodeDetails.getInt("episode");
			} catch (JSONException e) {
			}
		}
		if (episodeDetails.has("productioncode")) {
			try {
				row1 += " (" + episodeDetails.getString("productioncode" + ")");
			} catch (JSONException e) {
			}
		}

		if (episodeDetails.has("showtitle")) {
			try {
				row2 = episodeDetails.getString("showtitle");
			} catch (JSONException e) {
			}
		}

		if (episodeDetails.has("label")) {
			try {
				row3 = episodeDetails.getString("label");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (episodeDetails.has("originaltitle")) {
			try {
				String originalTitle = episodeDetails
						.getString("originaltitle");
				if (!originalTitle.isEmpty()) {
					row3 += " (" + originalTitle + ")";
				}
			} catch (JSONException e) {
			}
		}

		if (episodeDetails.has("rating")) {
			try {
				double rating = (double) (int) (episodeDetails
						.getDouble("rating") * 10) / 10.0f;
				row4 = "Rating: " + rating;
			} catch (Exception e) {
			}
		}

		PebbleCommand.sendMediaUpdate(context, row1, Constants.ROW_1);
		PebbleCommand.sendMediaUpdate(context, row2, Constants.ROW_2);
		PebbleCommand.sendMediaUpdate(context, row3, Constants.ROW_3);
		PebbleCommand.sendMediaUpdate(context, row4, Constants.ROW_4);
	}

	private void parseApplicationJSON(Context context, String method,
			JSONObject jsonObject) {
	}

	private void parseAudioLibraryJSON(Context context, String method,
			JSONObject jsonObject) {
	}

	private void parseGUIJSON(Context context, String method,
			JSONObject jsonObject) {
		if (method.equals("OnScreensaverActivated")) {
		} else if (method.equals("OnScreensaverDeactivated")) {
		}
	}

	private void parseInputJSON(Context context, String method,
			JSONObject jsonObject) {
	}

	private void parsePlayerJSON(Context context, String method,
			JSONObject jsonObject) {
		if (method.equals("OnPlay")) {
			PebbleCommand.startPlayer(context);
			try {
				JSONObject item = jsonObject.getJSONObject("params")
						.getJSONObject("data").getJSONObject("item");
				Log.d("XBMCPebbleTranslator", "OnPlay, item:" + item);
				if (item.has("id") && item.has("type")) {
					XBMCCommand.requestInfo(context, item.getInt("id"),
							item.getString("type"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else if (method.equals("OnPause")) {

		} else if (method.equals("OnStop")) {
			PebbleCommand.stopPlayer(context);
		}
	}

	private void parsePlaylistJSON(Context context, String method,
			JSONObject jsonObject) {
	}

	private void parseSystemJSON(Context context, String method,
			JSONObject jsonObject) {
	}

	private void parseVideoLibraryJSON(Context context, String method,
			JSONObject jsonObject) {
		if (method.equals("GetEpisodeDetails")) {
			Log.d("videoLibrary", "parsing video library stuff");
			Intent intent = new Intent(context, PebbleService.class);
			intent.setAction("org.muellerpete.pebblebmc.action.XBMC_PLAYER_ACTION");
			intent.putExtra("action", "OnMediaUpdate");
			intent.putExtra("infoRow1", "Hello");
			intent.putExtra("infoRow2", "my darling");
			intent.putExtra("infoRow3", "ragtime");
			intent.putExtra("infoRow4", "girl");
			context.startService(intent);
		}
	}

	private void parseXBMCJSON(Context context, String method,
			JSONObject jsonObject) {
	}
}
