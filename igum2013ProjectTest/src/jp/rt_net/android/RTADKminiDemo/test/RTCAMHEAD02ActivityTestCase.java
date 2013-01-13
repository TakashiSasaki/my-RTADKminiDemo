package jp.rt_net.android.RTADKminiDemo.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;
import jp.rt_net.android.RTADKminiDemo.RTCAMHEAD02Activity;
import junit.framework.Assert;

public class RTCAMHEAD02ActivityTestCase extends
		ActivityInstrumentationTestCase2<RTCAMHEAD02Activity> {

	RTCAMHEAD02Activity rtCamHead02Activity;

	public void test() {
		this.rtCamHead02Activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

			}
		});
	}// test

	public void testLastJpeg() {
		this.rtCamHead02Activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				byte[] last_jpeg_byte_array = RTCAMHEAD02ActivityTestCase.this.rtCamHead02Activity
						.getLastJpegByteArray();
				Assert.assertNotNull(last_jpeg_byte_array);
				Bitmap bitmap = BitmapFactory.decodeByteArray(
						last_jpeg_byte_array, 0, last_jpeg_byte_array.length);
				Assert.assertNotNull(bitmap);
				Assert.assertEquals(bitmap.getHeight(),
						RTCAMHEAD02ActivityTestCase.this.rtCamHead02Activity
								.getLastPreviewSize().height);
				Assert.assertEquals(bitmap.getWidth(),
						RTCAMHEAD02ActivityTestCase.this.rtCamHead02Activity
								.getLastPreviewSize().width);
			}
		});
	}// testLastJpeg

	public RTCAMHEAD02ActivityTestCase() {
		super(RTCAMHEAD02Activity.class);
	} // a default constructor

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.rtCamHead02Activity = this.getActivity();
	}// setUp

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}// tearDown

}// RTCAMHEAD02ActivityTestCase
