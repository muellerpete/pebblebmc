package org.muellerpete.pebblebmc.translator;

import org.muellerpete.pebblebmc.pebble.Constants;
import org.muellerpete.pebblebmc.xbmc.XBMCCommand;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PebbleXBMCTranslator extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action
				.equals("org.muellerpete.pebblebmc.action.pebble.WINDOW_ACTION")) {
			// TODO on player show request info
		}
		if (action
				.equals("org.muellerpete.pebblebmc.action.pebble.BUTTON_ACTION")) {
			int window = (int) intent.getLongExtra("window", -1);
			int button = (int) intent.getLongExtra("button", -1);
			Log.d("PebbleXBMCTranslator", "button: " + button);
			boolean isLong = intent.getBooleanExtra("isLong", false);
			int clicks = (int) intent.getLongExtra("clicks", -1);
			if (window == Constants.NAVIGATOR_WINDOW) {
				Log.d("PebbleXBMCTranslator", "navigator window");
				if (button == Constants.BUTTON_ID_BACK) {
					if (isLong) {
						Log.d("PebbleXBMCTranslator", "long back click");
					} else if (clicks == 1) {
						Log.d("PebbleXBMCTranslator", "single back click");
						XBMCCommand.back(context);
					} else if (clicks > 1) {
						Log.d("PebbleXBMCTranslator", clicks + " back clicks");
					}
				}

				if (button == Constants.BUTTON_ID_UP) {
					if (isLong) {
						Log.d("PebbleXBMCTranslator", "long up click");
					} else if (clicks == 1) {
						Log.d("PebbleXBMCTranslator", "single up click");
						XBMCCommand.up(context);
					} else if (clicks == 2) {
						XBMCCommand.right(context);
					}
				}

				if (button == Constants.BUTTON_ID_SELECT) {
					if (isLong) {
						Log.d("PebbleXBMCTranslator", "long select click");
					} else if (clicks == 1) {
						Log.d("PebbleXBMCTranslator", "single select click");
						XBMCCommand.select(context);
						XBMCCommand.GUIGetProperties(context);
					} else if (clicks > 1) {
						Log.d("PebbleXBMCTranslator", clicks + " select clicks");

					}
				}

				if (button == Constants.BUTTON_ID_DOWN) {
					if (isLong) {
						Log.d("PebbleXBMCTranslator", "long down click");
					} else if (clicks == 1) {
						Log.d("PebbleXBMCTranslator", "single down click");
						XBMCCommand.down(context);
					} else if (clicks == 2) {
						Log.d("PebbleXBMCTranslator", "two down clicks");
						XBMCCommand.left(context);
					}
				}
			} else if (window == Constants.PLAYER_WINDOW) {
				Log.d("PebbleXBMCTranslator", "player window");

				if (button == Constants.BUTTON_ID_BACK) {
					if (isLong) {
						Log.d("PebbleXBMCTranslator", "long back click");
					} else if (clicks == 1) {
						Log.d("PebbleXBMCTranslator", "single back click");
						XBMCCommand.back(context);
					} else if (clicks > 1) {
						Log.d("PebbleXBMCTranslator", clicks + " back clicks");
					}
				}

				if (button == Constants.BUTTON_ID_UP) {
					if (isLong) {
						Log.d("PebbleXBMCTranslator", "long up click");
					} else if (clicks == 1) {
						Log.d("PebbleXBMCTranslator", "single up click");
						XBMCCommand.stepBack(context);
					} else if (clicks == 2) {
						Log.d("PebbleXBMCTranslator", "two up clicks");
						XBMCCommand.volumeUp(context, 10);
					} else if (clicks == 3) {
						Log.d("PebbleXBMCTranslator", "three up clicks");
						XBMCCommand.bigStepBack(context);
					}
				}

				if (button == Constants.BUTTON_ID_SELECT) {
					if (isLong) {
						Log.d("PebbleXBMCTranslator", "long select click");
					} else if (clicks == 1) {
						Log.d("PebbleXBMCTranslator", "single select click");
						XBMCCommand.playPause(context);
					} else if (clicks > 1) {
						Log.d("PebbleXBMCTranslator", clicks + " select clicks");
					}
				}

				if (button == Constants.BUTTON_ID_DOWN) {
					if (isLong) {
						Log.d("PebbleXBMCTranslator", "long down click");
					} else if (clicks == 1) {
						Log.d("PebbleXBMCTranslator", "single down click");
						XBMCCommand.stepForward(context);
					} else if (clicks == 2) {
						Log.d("PebbleXBMCTranslator", "two down clicks");
						XBMCCommand.volumeDown(context, 10);
					} else if (clicks == 3) {
						Log.d("PebbleXBMCTranslator", "three up clicks");
						XBMCCommand.bigStepForward(context);
					}
				}
			}

		}
	}
}
