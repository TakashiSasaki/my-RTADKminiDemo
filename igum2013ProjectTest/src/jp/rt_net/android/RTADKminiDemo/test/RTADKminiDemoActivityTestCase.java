package jp.rt_net.android.RTADKminiDemo.test;

import jp.rt_net.android.RTADKminiDemo.RTADKminiDemoActivity;
import android.test.ActivityInstrumentationTestCase2;

public class RTADKminiDemoActivityTestCase extends
		ActivityInstrumentationTestCase2<RTADKminiDemoActivity> {

	RTADKminiDemoActivity rtAdkMiniDemoActivity;

	public void test() {
		this.rtAdkMiniDemoActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

			}
		});
	}// test

	public RTADKminiDemoActivityTestCase() {
		super(RTADKminiDemoActivity.class);
	} // a default constructor

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.rtAdkMiniDemoActivity = this.getActivity();
	}// setUp

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}// tearDown
}// RTADKminiDemoActivityTestCase

