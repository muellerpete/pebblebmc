package org.muellerpete.pebblebmc;

import org.muellerpete.pebblebmc.pebble.PebbleService;
import org.muellerpete.pebblebmc.xbmc.XBMCCommand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class Debug extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.debug, menu);
		return true;
	}

	public void pebbleStartPlayer(View view) {
		Intent startPlayer = new Intent(this.getApplicationContext(),
				PebbleService.class);
		startPlayer
				.setAction("org.muellerpete.pebblebmc.action.XBMC_PLAYER_ACTION");
		startPlayer.putExtra("action", "OnPlay");
		startService(startPlayer);
	}

	public void pebbleSendMediaUpdate(View view) {

		// PebbleCommand.sendMediaUpdate(view.getContext(), "AAAAA",
		// Constants.ROW_1);
		// PebbleCommand.sendMediaUpdate(view.getContext(), "BBBBBB",
		// Constants.ROW_2);
		// PebbleCommand
		// .sendMediaUpdate(view.getContext(), "CCC", Constants.ROW_3);

		// PebbleCommand.sendMediaUpdate(view.getContext(), "DDDDD",
		// Constants.ROW_4);
	}

	public void sendAction(View view) {
		Button button = ((Button) view);
		XBMCCommand.sendAction(this, button.getText().toString());
	}
}
