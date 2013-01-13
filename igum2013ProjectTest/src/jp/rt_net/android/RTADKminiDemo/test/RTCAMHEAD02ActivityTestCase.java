package jp.rt_net.android.RTADKminiDemo.test;

import android.test.ActivityInstrumentationTestCase2;
import jp.rt_net.android.RTADKminiDemo.RTCAMHEAD02Activity;

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

}
