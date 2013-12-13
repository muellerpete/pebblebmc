package org.muellerpete.pebblebmc.pebble;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

public class PebbleService extends Service {

	private PebbleKit.PebbleDataReceiver dataReceiver;
	private PebbleKit.PebbleAckReceiver ackReceiver;
	private PebbleKit.PebbleNackReceiver nackReceiver;

	private final MessageManager messageManager = new MessageManager();
	private final static int MAX_LENGTH = 82;
	private final static UUID APP_UUID = UUID
			.fromString("eee65760-8ea9-41d1-ac84-de8de91595aa");

	@Override
	public void onCreate() {
		super.onCreate();
		new Thread(messageManager).start();
		dataReceiver = new PebbleKit.PebbleDataReceiver(APP_UUID) {
			@Override
			public void receiveData(final Context context,
					final int transactionId, final PebbleDictionary data) {
				PebbleKit.sendAckToPebble(context, transactionId);
				Log.d("PebbleService", "got button: " + data.toJsonString());
				Intent receivedDataIntent = new Intent(
						"org.muellerpete.pebblebmc.action.pebble.BUTTON_ACTION");
				receivedDataIntent.putExtra("button",
						data.getUnsignedInteger(Constants.BUTTON));
				receivedDataIntent.putExtra("window",
						data.getUnsignedInteger(Constants.WINDOW));
				receivedDataIntent
						.putExtra(
								"isLong",
								data.getUnsignedInteger(Constants.BUTTON_LONG) == Constants.TRUE);
				receivedDataIntent.putExtra("clicks",
						data.getUnsignedInteger(Constants.BUTTON_NUM_CLICKS));
				context.sendBroadcast(receivedDataIntent);
			}
		};

		PebbleKit.registerReceivedDataHandler(this, dataReceiver);

		ackReceiver = new PebbleKit.PebbleAckReceiver(APP_UUID) {
			@Override
			public void receiveAck(final Context context,
					final int transactionId) {
				messageManager.notifyAckReceivedAsync();
			}
		};

		PebbleKit.registerReceivedAckHandler(this, ackReceiver);

		nackReceiver = new PebbleKit.PebbleNackReceiver(APP_UUID) {
			@Override
			public void receiveNack(final Context context,
					final int transactionId) {
				messageManager.notifyNackReceivedAsync();
			}
		};

		PebbleKit.registerReceivedNackHandler(this, nackReceiver);
	}

	public void startPlayer() {
		if (true) {
			PebbleKit.startAppOnPebble(getApplicationContext(), APP_UUID);
		}
		PebbleDictionary startPlayerDictionary = new PebbleDictionary();
		startPlayerDictionary.addUint8(Constants.SHOW_PLAYER, Constants.TRUE);
		messageManager.offer(startPlayerDictionary);

	}

	public void stopPlayer() {
		PebbleDictionary startPlayerDictionary = new PebbleDictionary();
		startPlayerDictionary.addUint8(Constants.SHOW_PLAYER, Constants.FALSE);
		messageManager.offer(startPlayerDictionary);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(dataReceiver);
		unregisterReceiver(ackReceiver);
		unregisterReceiver(nackReceiver);
	}

	private static String trimString(String string, int maxLength) {
		return string.substring(0, Math.min(string.length(), maxLength));
	}

	@Override
	public int onStartCommand(Intent intent, int flag, int startId) {
		String action = intent.getAction();
		if (action
				.equals("org.muellerpete.pebblebmc.action.pebble.MEDIA_UPDATE")) {
			byte row = intent.getByteExtra("row", (byte) 0x0);
			String text = trimString(intent.getStringExtra("text"), MAX_LENGTH);
			PebbleDictionary mediaUpdateDictionary = new PebbleDictionary();
			mediaUpdateDictionary.addString(row, text);
			messageManager.offer(mediaUpdateDictionary);
		} else if (action
				.equals("org.muellerpete.pebblebmc.action.pebble.START_PLAYER")) {
			startPlayer();
		} else if (action
				.equals("org.muellerpete.pebblebmc.action.pebble.STOP_PLAYER")) {
			stopPlayer();
		} else if (action.equals("com.getpebble.action.PEBBLE_CONNECTED")) {
		} else if (action.equals("com.getpebble.action.PEBBLE_DISCONNECTED")) {
		}
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public class MessageManager implements Runnable {
		public Handler messageHandler;
		private final BlockingQueue<PebbleDictionary> messageQueue = new LinkedBlockingQueue<PebbleDictionary>();
		private Boolean isMessagePending = Boolean.valueOf(false);

		@Override
		public void run() {
			Looper.prepare();
			messageHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					Log.w(this.getClass().getSimpleName(),
							"Please post() your blocking runnables to Mr Manager, "
									+ "don't use sendMessage()");
				}

			};
			Looper.loop();
		}

		private void consumeAsync() {
			messageHandler.post(new Runnable() {
				@Override
				public void run() {
					synchronized (isMessagePending) {
						if (isMessagePending.booleanValue()) {
							return;
						}

						synchronized (messageQueue) {
							if (messageQueue.size() == 0) {
								return;
							}
							PebbleKit.sendDataToPebble(getApplicationContext(),
									APP_UUID, messageQueue.peek());
						}

						isMessagePending = Boolean.valueOf(true);
					}
				}
			});
		}

		public void notifyAckReceivedAsync() {
			messageHandler.post(new Runnable() {
				@Override
				public void run() {
					synchronized (isMessagePending) {
						isMessagePending = Boolean.valueOf(false);
					}
					if (!messageQueue.isEmpty()) {
						messageQueue.remove();
					}
				}
			});
			consumeAsync();
		}

		public void notifyNackReceivedAsync() {
			messageHandler.post(new Runnable() {
				@Override
				public void run() {
					synchronized (isMessagePending) {
						isMessagePending = Boolean.valueOf(false);
					}
				}
			});
			consumeAsync();
		}

		public boolean offer(final PebbleDictionary data) {
			final boolean success = messageQueue.offer(data);

			if (success) {
				consumeAsync();
			}

			return success;
		}
	}
}
