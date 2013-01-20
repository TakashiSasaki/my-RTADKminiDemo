package jp.rt_net.android.RTADKminiDemo;

import java.io.IOException;
import java.net.SocketException;

import com.stackoverflow.users.whome.Utils;

import jp.rt_net.android.RTADKminiDemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.content.Intent;
import android.hardware.Camera.Size;

public class RTCAMHEAD02Activity extends Activity {

	// RT-ADKminiとのデータのやり取りで使うコマンド
	private static final int USBAccessoryWhat = 0;
	public static final int PUSHBUTTON_STATUS_CHANGE = 2;
	public static final int SERVO_01 = 7;
	public static final int SERVO_02 = 8;

	// ボタンの押下情報取得用
	public static final int BUTTON_1_PRESSED = 0x01;
	public static final int BUTTON_2_PRESSED = 0x02;
	public static final int BUTTON_3_PRESSED = 0x04;
	public static final int BUTTON_4_PRESSED = 0x08;

	// サーボモータの角度情報
	static int servo01_pos = 0;
	static int servo02_pos = 0;

	USBAccessoryManager accessoryManager;

	Handler uiHandler;
	private SurfaceView mSurfaceView;
	private CameraView mCameraView;

	// UI
	SeekBar seekBar1;
	SeekBar seekBar2;
	TextView value_of_seekbar1;
	TextView value_of_seekbar2;
	private Button button1, button2;

	// network thread
	HttpServerThread httpServerThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.rtcamheadactivity);

		this.accessoryManager = new USBAccessoryManager(this.handler,
				USBAccessoryWhat);

		// 作成したCameraViewクラスをインスタンス化
		this.mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
		this.mCameraView = new CameraView(this, this.mSurfaceView);

		/******* SeekBar1の定義 *******/
		this.seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		this.value_of_seekbar1 = (TextView) findViewById(R.id.valueOfseekBar1);
		// シークバーの初期値をTextViewに表示
		this.value_of_seekbar1.setText("Current Value:"
				+ this.seekBar1.getProgress());
		// シークバーの現在地、初期値、セカンダリ値をリセット
		this.seekBar1.setMax(255);
		this.seekBar1.setProgress(0);
		this.seekBar1.setSecondaryProgress(0);
		// SeekBar1 の値が変更された時に呼び出されるコールバックを登録
		this.seekBar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			// SeekBar1 の値が変わった時の動作
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromTouch) {
				servo01_pos = progress; // servo01の角度はseekBarのprogressで決まる
				RTCAMHEAD02Activity.this.value_of_seekbar1
						.setText("Current Value:" + progress); // textにseekbar1を表示
				RTCAMHEAD02Activity.this.uiHandler = RTCAMHEAD02Activity.this.handler;

				Message servo01Update = Message.obtain(
						RTCAMHEAD02Activity.this.uiHandler, SERVO_01);
				if (RTCAMHEAD02Activity.this.uiHandler != null) {
					RTCAMHEAD02Activity.this.uiHandler
							.sendMessage(servo01Update);
				}
			}

			// SeekBar のタッチの開始時の動作
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			// SeekBar のタッチの終了時の動作
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		/******* SeekBar2の定義 *******/
		this.seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
		this.value_of_seekbar2 = (TextView) findViewById(R.id.valueOfseekBar2);
		// シークバーの初期値をTextViewに表示
		this.value_of_seekbar2.setText("Current Value:"
				+ this.seekBar2.getProgress());
		// シークバーの現在地、初期値、セカンダリ値をセット
		this.seekBar2.setMax(255);
		this.seekBar2.setProgress(0);
		this.seekBar2.setSecondaryProgress(0);
		// SeekBar の値が変更された時に呼び出されるコールバックを登録
		this.seekBar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			// SeekBar2 の値が変わった時の動作
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromTouch) {
				servo02_pos = progress;
				RTCAMHEAD02Activity.this.value_of_seekbar2
						.setText("Current Value:" + progress);
				RTCAMHEAD02Activity.this.uiHandler = RTCAMHEAD02Activity.this.handler;

				Message servo02Update = Message.obtain(
						RTCAMHEAD02Activity.this.uiHandler, SERVO_02);
				if (RTCAMHEAD02Activity.this.uiHandler != null) {
					RTCAMHEAD02Activity.this.uiHandler
							.sendMessage(servo02Update);
				}
			}

			// SeekBar のタッチの開始時の動作
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			// SeekBar のタッチの終了時の動作
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		/******* originボタンの定義 *******/
		this.button1 = (Button) findViewById(R.id.OriginButton);

		this.button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// originボタンがクリックされた時に呼び出される
				RTCAMHEAD02Activity.this.seekBar1.setProgress(127);// サーボモータ01を強制的に原点に戻す
				RTCAMHEAD02Activity.this.seekBar2.setProgress(127);// サーボモータ02を強制的に原点に戻す
			}
		});

		/******* CamChangeボタンの定義 *******/
		this.button2 = (Button) findViewById(R.id.CamChangeButton);

		this.button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// CamChangeボタンがクリックされた時に呼び出される
				// mCameraView.cameraChange(); // カメラの内外を切り替える
				Intent i = new Intent(getApplicationContext(),
						RTADKminiDemoActivity.class);
				startActivity(i);
			}
		});

		try {
			this.httpServerThread = new HttpServerThread(this.mCameraView,
					getAssets());
			this.httpServerThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}// try

	}// onCreate

	@Override
	public void onStart() {
		super.onStart();
		String url = "";
		try {
			url = getUrl();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		this.setTitle(url + " RT-ADKmini デバイスが接続されていません");
	}// onStart

	static String getUrl() throws SocketException {
		String ip_address = Utils.getIPAddress(true);
		return "http://" + ip_address + ":12346/camera.html";
	}

	@Override
	public void onResume() {
		super.onResume();
		this.accessoryManager.enable(this, getIntent());
	}

	@Override
	public void onPause() {
		super.onPause();
		this.accessoryManager.disable(this);
		disconnectAccessory();
	}

	// RT-ADKminiが未接続時に呼ばれる
	public void disconnectAccessory() {
		String url = "";
		try {
			url = getUrl();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		this.setTitle(url + " RT-ADKmini デバイスが接続されていません");
	}

	/******* USB Manager thread　または　UIからの　messageを受け取り処理するhandler *******/
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			byte[] commandPacket = new byte[2];

			switch (msg.what) {
			case SERVO_01:
				if (RTCAMHEAD02Activity.this.accessoryManager.isConnected() == false) {
					return;
				}

				commandPacket[0] = SERVO_01;
				commandPacket[1] = (byte) servo01_pos;
				RTCAMHEAD02Activity.this.accessoryManager.write(commandPacket);
				break;
			case SERVO_02:
				if (RTCAMHEAD02Activity.this.accessoryManager.isConnected() == false) {
					return;
				}

				commandPacket[0] = SERVO_02;
				commandPacket[1] = (byte) servo02_pos;
				RTCAMHEAD02Activity.this.accessoryManager.write(commandPacket);
				break;

			case USBAccessoryWhat:
				switch (((USBAccessoryManagerMessage) msg.obj).type) {
				case READ:
					if (RTCAMHEAD02Activity.this.accessoryManager.isConnected() == false) {
						return;
					}

					while (true) {
						if (RTCAMHEAD02Activity.this.accessoryManager
								.available() < 2) {
							// コマンドの最大サイズは2byteなのでバッファのサイズが2btyeよりも小さくなったら抜ける
							break;
						}

						// commandPacketのバッファのデータの書き込み、
						// 読み込んだデータを消去する
						RTCAMHEAD02Activity.this.accessoryManager
								.read(commandPacket);

						switch (commandPacket[0]) {
						// DIN0,1,2,3につながっているタクトスイッチが押された時の処理
						case PUSHBUTTON_STATUS_CHANGE:
							if ((commandPacket[1] & BUTTON_1_PRESSED) == BUTTON_1_PRESSED) {
								servo01_pos += 10;
								if (servo01_pos >= 255)
									servo01_pos = 255;
								RTCAMHEAD02Activity.this.seekBar1
										.setProgress(servo01_pos);
							}
							if ((commandPacket[1] & BUTTON_2_PRESSED) == BUTTON_2_PRESSED) {
								servo01_pos -= 10;
								if (servo01_pos <= 0)
									servo01_pos = 0;
								RTCAMHEAD02Activity.this.seekBar1
										.setProgress(servo01_pos);
							}
							if ((commandPacket[1] & BUTTON_3_PRESSED) == BUTTON_3_PRESSED) {
								servo02_pos += 10;
								if (servo02_pos >= 255)
									servo02_pos = 255;
								RTCAMHEAD02Activity.this.seekBar2
										.setProgress(servo02_pos);
							}
							if ((commandPacket[1] & BUTTON_4_PRESSED) == BUTTON_4_PRESSED) {
								servo02_pos -= 10;
								if (servo02_pos <= 0)
									servo02_pos = 0;
								RTCAMHEAD02Activity.this.seekBar2
										.setProgress(servo02_pos);
							}
							break;
						}
					}// while
					break;
				case CONNECTED:
					break;
				case READY:
					String url = "";
					try {
						url = getUrl();
					} catch (SocketException e) {
						e.printStackTrace();
					}
					setTitle(url + " RT-ADKmini デバイスが接続されました");
					break;
				case DISCONNECTED:
					disconnectAccessory();
					break;
				} // switch(~.type)
				break;
			default:
				break;
			} // switch(msg.what)
		} // handleMessage
	}; // handler

	public byte[] getLastJpegByteArray() {
		return this.mCameraView.getLastJpegByteArray();
	}// getLastJpegByteArray

	public Size getLastPreviewSize() {
		return this.mCameraView.lastPreviewSize;
	}// getLastPreviewSize

	public boolean isYuv420() {
		return this.mCameraView.isYuv420();
	}

	public boolean isYuv422() {
		return this.mCameraView.isYuv422();
	}

}// RTCAMHEAD02Activity