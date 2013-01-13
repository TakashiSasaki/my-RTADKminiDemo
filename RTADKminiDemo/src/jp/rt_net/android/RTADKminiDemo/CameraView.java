package jp.rt_net.android.RTADKminiDemo;

import java.io.ByteArrayOutputStream;

import info.justoneplanet.android.camera.Util;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

//カメラの制御
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
	private int cameraId = 0; // カメラの種類
	private SurfaceHolder holder; // ホルダ
	Camera camera; // カメラ
	protected Context context;
	byte[] lastJpegByteArray;
	Size lastPreviewSize;
	byte[] lastRawPreviewData;
	volatile boolean inCallBack;

	// コントラスタ
	public CameraView(Context context, SurfaceView sv) {
		super(context);
		this.context = context;
		// サーフェイス・ホルダの生成
		this.holder = sv.getHolder();
		this.holder.addCallback(this);

		// プッシュ・バッファの指定
		this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}// a constructor

	// サーフェス生成イベントの処理
	@Override
	public void surfaceCreated(SurfaceHolder holder1) {
		// カメラの初期化
		try {
			this.camera = Camera.open(this.cameraId);
			this.camera.setPreviewDisplay(holder1);

		} catch (Exception e) {
			e.printStackTrace();
		}// try
	}// surfaceCreated

	// サーフェス変更イベントの処理
	@Override
	public void surfaceChanged(SurfaceHolder holder1, int format, int width,
			int height) {
		// カメラプレビューの開始
		this.camera.setPreviewCallback(new Camera.PreviewCallback() {
			@Override
			synchronized public void onPreviewFrame(byte[] data, Camera camera1) {
				CameraView.this.inCallBack = false;
				if (data == null)
					return;
				// camera1.stopPreview();
				camera1.setPreviewCallback(null);

				CameraView.this.lastRawPreviewData = new byte[data.length];
				for (int i = 0; i < data.length; ++i) {
					CameraView.this.lastRawPreviewData[i] = data[i];
				}// for

				CameraView.this.lastPreviewSize = camera1.getParameters()
						.getPreviewSize();
				int[] rgb_array = Util.decodeYUV(data,
						CameraView.this.lastPreviewSize.width,
						CameraView.this.lastPreviewSize.height);
				Bitmap bitmap = Bitmap.createBitmap(rgb_array,
						CameraView.this.lastPreviewSize.width,
						CameraView.this.lastPreviewSize.height,
						Config.ARGB_8888);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.JPEG, 80, baos);
				// bitmap.recycle();
				CameraView.this.lastJpegByteArray = baos.toByteArray();
				camera1.setPreviewCallback(this);
				CameraView.this.inCallBack = false;
				// camera1.startPreview();
			}// onPreviewFrame
		});
		// this.camera.setPreviewCallback(null);
		this.camera.startPreview();
	}// surfaceChanged

	// サーフェス開放イベントの処理
	@Override
	public void surfaceDestroyed(SurfaceHolder holder1) {
		// カメラのプレビュー停止
		this.camera.setPreviewCallback(null);
		this.camera.stopPreview();

		while (this.inCallBack) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}// try
		}// while

		this.camera.release();
		this.camera = null;
	}// surfaceDestroyed

	public byte[] getLastJpegByteArray() {
		return this.lastJpegByteArray;
	}// getLastJpegByteArray

	// カメラの切り替えをする時の処理
	public void cameraChange() {
		// サポートしているカメラの数を確かめる
		if (Camera.getNumberOfCameras() >= 1) {

			if (this.cameraId == 0)
				this.cameraId = 1;
			else
				this.cameraId = 0;

			this.camera.stopPreview();
			this.camera.release();
			this.camera = Camera.open(this.cameraId);
			try {
				this.camera.setPreviewDisplay(this.holder);
			} catch (Exception e) {
				e.printStackTrace();
			}// try
		} else {
			Toast.makeText(this.context.getApplicationContext(),
					"サポートしているカメラは一台です", Toast.LENGTH_SHORT).show();
		}// if
		this.camera.startPreview();
	}// cameraChange

	public boolean isYuv422() {
		final int n_bytes_from_size = this.lastPreviewSize.height
				* this.lastPreviewSize.width * 2;
		return this.lastRawPreviewData.length == n_bytes_from_size;
	}

	public boolean isYuv420() {
		final int n_bytes_from_size = this.lastPreviewSize.height
				* this.lastPreviewSize.width / 2 * 3;
		return this.lastRawPreviewData.length == n_bytes_from_size;
	}
}// CameraView
