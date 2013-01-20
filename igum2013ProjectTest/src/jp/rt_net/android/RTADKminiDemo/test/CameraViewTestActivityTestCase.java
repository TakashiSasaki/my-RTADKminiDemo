package jp.rt_net.android.RTADKminiDemo.test;

import jp.rt_net.android.RTADKminiDemo.CameraViewTestActivity;
import android.test.ActivityInstrumentationTestCase2;

public class CameraViewTestActivityTestCase extends
		ActivityInstrumentationTestCase2<CameraViewTestActivity> {
	CameraViewTestActivity cameraViewTestActivity;

	public void test() throws InterruptedException {
		this.cameraViewTestActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				//
			}
		});
		Thread.sleep(1000000);
		assertTrue(this.cameraViewTestActivity.getCameraView()
				.getLastCompressionResult());
	}

	public CameraViewTestActivityTestCase() {
		super(CameraViewTestActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		this.cameraViewTestActivity = this.getActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

}
