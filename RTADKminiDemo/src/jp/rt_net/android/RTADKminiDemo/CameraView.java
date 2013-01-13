package jp.rt_net.android.RTADKminiDemo;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

//カメラの制御
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
	private int cameraId = 0; // カメラの種類
	private SurfaceHolder holder; // ホルダ
	private Camera camera; // カメラ
	protected Context context;

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
		this.camera.startPreview();
	}// surfaceChanged

	// サーフェス開放イベントの処理
	@Override
	public void surfaceDestroyed(SurfaceHolder holder1) {
		// カメラのプレビュー停止
		this.camera.setPreviewCallback(null);
		this.camera.stopPreview();
		this.camera.release();
		this.camera = null;
	}// surfaceDestroyed

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

}// CameraView