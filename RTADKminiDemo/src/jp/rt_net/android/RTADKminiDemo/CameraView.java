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
		holder = sv.getHolder();
		holder.addCallback(this);

		// プッシュ・バッファの指定
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	// サーフェス生成イベントの処理
	public void surfaceCreated(SurfaceHolder holder) {
		// カメラの初期化
		try {
			camera = Camera.open(cameraId);
			camera.setPreviewDisplay(holder);

		} catch (Exception e) {
		}

	}

	// サーフェス変更イベントの処理
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// カメラプレビューの開始
		camera.startPreview();
	}

	// サーフェス開放イベントの処理
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// カメラのプレビュー停止
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	// カメラの切り替えをする時の処理
	public void cameraChange() {
		// サポートしているカメラの数を確かめる
		if (Camera.getNumberOfCameras() >= 1) {

			if (cameraId == 0)
				cameraId = 1;
			else
				cameraId = 0;

			camera.stopPreview();
			camera.release();
			camera = Camera.open(cameraId);
			try {
				camera.setPreviewDisplay(holder);
			} catch (Exception e) {

			}
		} else {
			Toast.makeText(context.getApplicationContext(), "サポートしているカメラは一台です",
					Toast.LENGTH_SHORT).show();
		}
		camera.startPreview();
	}

}