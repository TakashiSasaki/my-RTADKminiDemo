package jp.rt_net.android.RTADKminiDemo;

import jp.rt_net.android.RTADKminiDemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class RTADKminiDemoActivity extends Activity {
	private final static int USBAccessoryWhat = 0;

	public static final int UPDATE_OUTPUTPIN_SETTING = 1;
	public static final int PUSHBUTTON_STATUS_CHANGE = 2;
	public static final int POT0_STATUS_CHANGE = 3;
	public static final int POT1_STATUS_CHANGE = 4;
	public static final int POT2_STATUS_CHANGE = 5;
	public static final int POT3_STATUS_CHANGE = 6;
	public static final int SERVO_01 = 7;
	public static final int SERVO_02 = 8;

	public static final int LED_0_ON = 0x01;
	public static final int LED_1_ON = 0x02;
	public static final int LED_2_ON = 0x04;
	public static final int LED_3_ON = 0x08;
	public static final int LED_4_ON = 0x10;
	public static final int LED_5_ON = 0x20;
	public static final int LED_6_ON = 0x40;
	public static final int LED_7_ON = 0x80;

	public static final int BUTTON_1_PRESSED = 0x01;
	public static final int BUTTON_2_PRESSED = 0x02;
	public static final int BUTTON_3_PRESSED = 0x04;
	public static final int BUTTON_4_PRESSED = 0x08;

	private int servo01_pos = 0;
	private int servo02_pos = 0;
	private USBAccessoryManager accessoryManager;

	SeekBar seekBar1, seekBar2;
	TextView value_of_seekbar1;
	TextView value_of_seekbar2;

	private Handler uiHandler;

	// private Button button5;

	/** Called when the activity is first created. */

	// public class SampleActivity1 extends Activity implements OnClickListener
	// {
	/** Called when the activity is first created. */

	// }

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.main);

		Button button5 = (Button) findViewById(R.id.button5);
		button5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.button5:
					Intent i = new Intent(getApplicationContext(),
							RTCAMHEAD02Activity.class);
					startActivity(i);
					break;
				}
			}
		});

		Log.i("i", "aaa");

		accessoryManager = new USBAccessoryManager(handler, USBAccessoryWhat);

		try {
			// Set the link to the message handler for this class
			LEDControl ledControl;

			ledControl = ((LEDControl) findViewById(R.id.led_0));
			ledControl.setHandler(handler);

			ledControl = ((LEDControl) findViewById(R.id.led_1));
			ledControl.setHandler(handler);

			ledControl = ((LEDControl) findViewById(R.id.led_2));
			ledControl.setHandler(handler);

			ledControl = ((LEDControl) findViewById(R.id.led_3));
			ledControl.setHandler(handler);

			ledControl = ((LEDControl) findViewById(R.id.led_4));
			ledControl.setHandler(handler);

			ledControl = ((LEDControl) findViewById(R.id.led_5));
			ledControl.setHandler(handler);

		} catch (Exception e) {
		}

		// Restore UI state from the savedInstanceState
		// If the savedInstanceState Bundle exists, then there is saved data to
		// restore.
		if (savedInstanceState != null) {
			try {
				// Restore the saved data for each of the LEDs.
				LEDControl ledControl;
				ProgressBar progressBar0, progressBar1, progressBar2, progressBar3;

				updateButton(R.id.button4,
						savedInstanceState.getBoolean("BUTTON4"));
				updateButton(R.id.button3,
						savedInstanceState.getBoolean("BUTTON3"));
				updateButton(R.id.button2,
						savedInstanceState.getBoolean("BUTTON2"));
				updateButton(R.id.button1,
						savedInstanceState.getBoolean("BUTTON1"));

				progressBar0 = (ProgressBar) findViewById(R.id.progress_bar0);
				progressBar0.setProgress(savedInstanceState.getInt("POT0"));
				progressBar1 = (ProgressBar) findViewById(R.id.progress_bar1);
				progressBar1.setProgress(savedInstanceState.getInt("POT1"));
				progressBar2 = (ProgressBar) findViewById(R.id.progress_bar2);
				progressBar2.setProgress(savedInstanceState.getInt("POT2"));
				progressBar3 = (ProgressBar) findViewById(R.id.progress_bar3);
				progressBar3.setProgress(savedInstanceState.getInt("POT3"));

				ledControl = (LEDControl) findViewById(R.id.led_5);
				ledControl.setState(savedInstanceState.getBoolean("LED5"));

				ledControl = (LEDControl) findViewById(R.id.led_4);
				ledControl.setState(savedInstanceState.getBoolean("LED4"));

				ledControl = (LEDControl) findViewById(R.id.led_3);
				ledControl.setState(savedInstanceState.getBoolean("LED3"));

				ledControl = (LEDControl) findViewById(R.id.led_2);
				ledControl.setState(savedInstanceState.getBoolean("LED2"));

				ledControl = (LEDControl) findViewById(R.id.led_1);
				ledControl.setState(savedInstanceState.getBoolean("LED1"));

				ledControl = (LEDControl) findViewById(R.id.led_0);
				ledControl.setState(savedInstanceState.getBoolean("LED0"));

			} catch (Exception e) {
				// Just in case there is some way for the savedInstanceState to
				// exist but for a single
				// item not to exist, lets catch any exceptions that might come.
			}
		}
		Log.i("i", "bbb");
		// Definition of SeekBar1
		seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		value_of_seekbar1 = (TextView) findViewById(R.id.TextView7);
		value_of_seekbar1.setText("Current Value:" + seekBar1.getProgress());
		seekBar1.setMax(255);
		seekBar1.setProgress(0);
		seekBar1.setSecondaryProgress(0);
		seekBar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromTouch) {
				servo01_pos = progress;
				value_of_seekbar1.setText("Current Value:" + progress);
				uiHandler = handler;

				Message servo01Update = Message.obtain(uiHandler, SERVO_01);
				if (uiHandler != null) {
					uiHandler.sendMessage(servo01Update);
				}

			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		// //////////////////////////////
		// Definition of SeekBar2
		seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
		value_of_seekbar2 = (TextView) findViewById(R.id.TextView8);
		value_of_seekbar2.setText("Current Value:" + seekBar2.getProgress());
		seekBar2.setMax(255);
		seekBar2.setProgress(0);
		seekBar2.setSecondaryProgress(0);
		seekBar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromTouch) {
				servo02_pos = progress;
				value_of_seekbar2.setText("Current Value:" + progress);
				uiHandler = handler;

				Message servo02Update = Message.obtain(uiHandler, SERVO_02);
				if (uiHandler != null) {
					uiHandler.sendMessage(servo02Update);
				}

			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();

		this.setTitle("RT-ADKminiDemo　デバイスが接続されていません");
	}

	@Override
	public void onResume() {
		super.onResume();
		accessoryManager.enable(this, getIntent());
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save the UI state into the savedInstanceState Bundle.
		// We only need to save the state of the LEDs since they are the only
		// control.
		// The state of the potentiometer and push buttons can be read and
		// restored
		// from their current hardware state.

		savedInstanceState.putBoolean("LED0",
				((LEDControl) findViewById(R.id.led_0)).getState());
		savedInstanceState.putBoolean("LED1",
				((LEDControl) findViewById(R.id.led_1)).getState());
		savedInstanceState.putBoolean("LED2",
				((LEDControl) findViewById(R.id.led_2)).getState());
		savedInstanceState.putBoolean("LED3",
				((LEDControl) findViewById(R.id.led_3)).getState());
		savedInstanceState.putBoolean("LED4",
				((LEDControl) findViewById(R.id.led_4)).getState());
		savedInstanceState.putBoolean("LED5",
				((LEDControl) findViewById(R.id.led_5)).getState());

		savedInstanceState.putInt("POT0",
				((ProgressBar) findViewById(R.id.progress_bar0)).getProgress());
		savedInstanceState.putInt("POT1",
				((ProgressBar) findViewById(R.id.progress_bar1)).getProgress());
		savedInstanceState.putInt("POT2",
				((ProgressBar) findViewById(R.id.progress_bar2)).getProgress());
		savedInstanceState.putInt("POT3",
				((ProgressBar) findViewById(R.id.progress_bar3)).getProgress());

		savedInstanceState.putBoolean("BUTTON1", isButtonPressed(R.id.button1));
		savedInstanceState.putBoolean("BUTTON2", isButtonPressed(R.id.button2));
		savedInstanceState.putBoolean("BUTTON3", isButtonPressed(R.id.button3));
		savedInstanceState.putBoolean("BUTTON4", isButtonPressed(R.id.button4));

		// Call the super function that we are over writing now that we have
		// saved our data.
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();
		accessoryManager.disable(this);
		disconnectAccessory();
	}

	/**
	 * Resets the demo application when a device detaches
	 */
	public void disconnectAccessory() {
		this.setTitle("RT-ADKminiDemo　デバイスが接続されていません");

		LEDControl ledControl;
		ProgressBar progressBar0, progressBar1, progressBar2, progressBar3;

		updateButton(R.id.button4, false);
		updateButton(R.id.button3, false);
		updateButton(R.id.button2, false);
		updateButton(R.id.button1, false);

		progressBar0 = (ProgressBar) findViewById(R.id.progress_bar0);
		progressBar0.setProgress(0);
		progressBar1 = (ProgressBar) findViewById(R.id.progress_bar1);
		progressBar1.setProgress(0);
		progressBar2 = (ProgressBar) findViewById(R.id.progress_bar2);
		progressBar2.setProgress(0);
		progressBar3 = (ProgressBar) findViewById(R.id.progress_bar3);
		progressBar3.setProgress(0);

		ledControl = (LEDControl) findViewById(R.id.led_5);
		ledControl.setState(false);

		ledControl = (LEDControl) findViewById(R.id.led_4);
		ledControl.setState(false);

		ledControl = (LEDControl) findViewById(R.id.led_3);
		ledControl.setState(false);

		ledControl = (LEDControl) findViewById(R.id.led_2);
		ledControl.setState(false);

		ledControl = (LEDControl) findViewById(R.id.led_1);
		ledControl.setState(false);

		ledControl = (LEDControl) findViewById(R.id.led_0);
		ledControl.setState(false);
	}

	/**
	 * Handler for receiving messages from the USB Manager thread or the LED
	 * control modules
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			byte[] commandPacket = new byte[2];

			switch (msg.what) {
			case SERVO_01:
				if (accessoryManager.isConnected() == false) {
					return;
				}

				commandPacket[0] = SERVO_01;
				commandPacket[1] = (byte) servo01_pos;
				accessoryManager.write(commandPacket);
				break;
			case SERVO_02:
				if (accessoryManager.isConnected() == false) {
					return;
				}

				commandPacket[0] = SERVO_02;
				commandPacket[1] = (byte) servo02_pos;
				accessoryManager.write(commandPacket);
				break;
			case UPDATE_OUTPUTPIN_SETTING:
				if (accessoryManager.isConnected() == false) {
					return;
				}

				commandPacket[0] = UPDATE_OUTPUTPIN_SETTING;
				commandPacket[1] = 0;

				if (((LEDControl) findViewById(R.id.led_0)).getState()) {
					commandPacket[1] |= LED_0_ON;
				}

				if (((LEDControl) findViewById(R.id.led_1)).getState()) {
					commandPacket[1] |= LED_1_ON;
				}

				if (((LEDControl) findViewById(R.id.led_2)).getState()) {
					commandPacket[1] |= LED_2_ON;
				}

				if (((LEDControl) findViewById(R.id.led_3)).getState()) {
					commandPacket[1] |= LED_3_ON;
				}

				if (((LEDControl) findViewById(R.id.led_4)).getState()) {
					commandPacket[1] |= LED_4_ON;
				}

				if (((LEDControl) findViewById(R.id.led_5)).getState()) {
					commandPacket[1] |= LED_5_ON;
				}

				accessoryManager.write(commandPacket);
				break;

			case USBAccessoryWhat:
				switch (((USBAccessoryManagerMessage) msg.obj).type) {
				case READ:
					if (accessoryManager.isConnected() == false) {
						return;
					}

					while (true) {
						if (accessoryManager.available() < 2) {
							// All of our commands in this example are 2 bytes.
							// If there are less
							// than 2 bytes left, it is a partial command
							break;
						}

						accessoryManager.read(commandPacket);
						int value;
						value = 0x000000FF & ((int) (commandPacket[1]));

						switch (commandPacket[0]) {
						case POT0_STATUS_CHANGE:
							ProgressBar progressBar0 = (ProgressBar) findViewById(R.id.progress_bar0);

							TextView ain0text;
							ain0text = (TextView) findViewById(R.id.AIN0_text);
							ain0text.setText("AIN0=" + value);

							if ((value >= 0)
									&& (value <= progressBar0.getMax())) {
								progressBar0.setProgress(value);
							}
							break;
						case POT1_STATUS_CHANGE:
							ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progress_bar1);

							TextView ain1text;
							ain1text = (TextView) findViewById(R.id.AIN1_text);
							ain1text.setText("AIN1=" + value);

							if ((value >= 0)
									&& (value <= progressBar1.getMax())) {
								progressBar1.setProgress(value);
							}
							break;
						case POT2_STATUS_CHANGE:
							ProgressBar progressBar2 = (ProgressBar) findViewById(R.id.progress_bar2);

							TextView ain2text;
							ain2text = (TextView) findViewById(R.id.AIN2_text);
							ain2text.setText("AIN2=" + value + "（"
									+ (3.3 * value / 255.0 * 1000.0 - 600.0)
									/ 10.0 + "℃）");

							if ((value >= 0)
									&& (value <= progressBar2.getMax())) {
								progressBar2.setProgress(value);
							}
							break;
						case POT3_STATUS_CHANGE:
							ProgressBar progressBar3 = (ProgressBar) findViewById(R.id.progress_bar3);

							TextView ain3text;
							ain3text = (TextView) findViewById(R.id.AIN3_text);
							ain3text.setText("AIN3=" + value + "（"
									+ (3.3 * value / 255.0 * 1000.0 - 600.0)
									/ 10.0 + "℃）");

							if ((value >= 0)
									&& (value <= progressBar3.getMax())) {
								progressBar3.setProgress(value);
							}
							break;
						case PUSHBUTTON_STATUS_CHANGE:
							updateButton(
									R.id.button1,
									((commandPacket[1] & BUTTON_1_PRESSED) == BUTTON_1_PRESSED) ? true
											: false);
							updateButton(
									R.id.button2,
									((commandPacket[1] & BUTTON_2_PRESSED) == BUTTON_2_PRESSED) ? true
											: false);
							updateButton(
									R.id.button3,
									((commandPacket[1] & BUTTON_3_PRESSED) == BUTTON_3_PRESSED) ? true
											: false);
							updateButton(
									R.id.button4,
									((commandPacket[1] & BUTTON_4_PRESSED) == BUTTON_4_PRESSED) ? true
											: false);
							break;
						}

					}
					break;
				case CONNECTED:
					break;
				case READY:
					setTitle("RT-ADKminiDemo　デバイスが接続されました");
					break;
				case DISCONNECTED:
					disconnectAccessory();
					break;
				}

				break;
			default:
				break;
			} // switch
		} // handleMessage
	}; // handler

	private void updateButton(int id, boolean pressed) {
		TextView textviewToUpdate;
		LinearLayout layoutToUpdate;

		textviewToUpdate = (TextView) findViewById(id);
		layoutToUpdate = (LinearLayout) textviewToUpdate.getParent();

		if (pressed) {
			textviewToUpdate.setText(R.string.pressed);
			layoutToUpdate.setBackgroundResource(R.color.button_pressed);
		} else {
			textviewToUpdate.setText(R.string.not_pressed);
			layoutToUpdate.setBackgroundResource(0);
		}
	}

	private boolean isButtonPressed(int id) {
		TextView buttonTextView;
		String buttonText;

		buttonTextView = ((TextView) findViewById(id));
		buttonText = buttonTextView.getText().toString();
		return buttonText.equals(getString(R.string.pressed));
	}

}// RTADKminiDemoActivity
