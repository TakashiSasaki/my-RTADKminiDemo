package jp.rt_net.android.RTADKminiDemo;

import java.io.IOException;
import java.net.SocketException;

import com.stackoverflow.users.whome.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.ImageView;

public class CameraViewTestActivity extends Activity {

	private SurfaceView mSurfaceView;
	private CameraView mCameraView;
	private ImageView mImageView;
	ShowJpegImageThread showJpegImageThread;
	HttpServerThread httpServerThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.camera_view_test_activity);
		this.mSurfaceView = (SurfaceView) findViewById(R.id.surfaceViewTest);
		this.mImageView = (ImageView) findViewById(R.id.imageViewTest);
		this.mCameraView = new CameraView(this, this.mSurfaceView);
		this.showJpegImageThread = new ShowJpegImageThread(this.mImageView,
				this.mCameraView, new Handler());
		this.showJpegImageThread.start();

		try {
			this.httpServerThread = new HttpServerThread(this.mCameraView,
					getAssets());
			this.httpServerThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			String x = Utils.getIPAddress(true);
			Log.v(CameraViewTestActivity.class.toString(), x);
			setTitle(x);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public CameraView getCameraView() {
		return this.mCameraView;
	}
}// CameraViewTest

class ShowJpegImageThread extends Thread {

	ImageView imageView;
	CameraView cameraView;
	Handler handler;

	public ShowJpegImageThread(ImageView image_view, CameraView camera_view,
			Handler handler) {
		this.imageView = image_view;
		this.cameraView = camera_view;
		this.handler = handler;
	}// a constructor

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] jpegByteArray = this.cameraView.getLastJpegByteArray();
			if (jpegByteArray == null)
				continue;
			final Bitmap bitmap = BitmapFactory.decodeByteArray(jpegByteArray,
					0, jpegByteArray.length);
			this.handler.post(new Runnable() {

				@Override
				public void run() {
					ShowJpegImageThread.this.imageView.setImageBitmap(bitmap);
				}// run
			});
		}// while
	}// run
}// ShowJpegImageThread
