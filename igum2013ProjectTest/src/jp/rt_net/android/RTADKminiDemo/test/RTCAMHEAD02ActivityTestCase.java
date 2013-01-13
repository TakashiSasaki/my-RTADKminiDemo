package jp.rt_net.android.RTADKminiDemo.test;

import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stackoverflow.users.whome.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
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
		getInstrumentation().waitForIdleSync();
	}// test

	public void _testTitle() throws SocketException {
		Log.v("RTCAMHEAD02ActivityTestCase", "testTitle");
		assertNotNull(this.rtCamHead02Activity);
		final String ip_address = Utils.getIPAddress(true);
		this.rtCamHead02Activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				assertEquals(
						RTCAMHEAD02ActivityTestCase.this.rtCamHead02Activity
								.getTitle(), ip_address);
			}// run
		});
		getInstrumentation().waitForIdleSync();
	}// testTitle

	public void testLastJpeg() throws InterruptedException {
		// Thread.sleep(5000);
		assertNotNull(this.rtCamHead02Activity);
		this.rtCamHead02Activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertNotNull(RTCAMHEAD02ActivityTestCase.this.rtCamHead02Activity);
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
			}// run
		});
		getInstrumentation().waitForIdleSync();
	}// testLastJpeg

	public void testYuv420orYuv422() {
		this.rtCamHead02Activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final boolean is_yuv_420 = RTCAMHEAD02ActivityTestCase.this.rtCamHead02Activity
						.isYuv420();
				final boolean is_yuv_422 = RTCAMHEAD02ActivityTestCase.this.rtCamHead02Activity
						.isYuv422();
				assertTrue(is_yuv_422 && !is_yuv_420 || !is_yuv_422
						&& is_yuv_420);
				assertTrue(is_yuv_420);
				assertFalse(is_yuv_422);
			}// run
		});
		getInstrumentation().waitForIdleSync();
	}// testYuv420orYuv422

	public static void testGetIpAddress() throws SocketException {
		String ip_address = Utils.getIPAddress(true);
		assertNotNull(ip_address);
		Pattern pattern = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");
		Matcher matcher = pattern.matcher(ip_address);
		assertTrue(matcher.find());
		assertEquals(ip_address, Utils.getIPAddress(true));
	}// testGetIpAddress

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
