package jp.rt_net.android.RTADKminiDemo;

import java.net.SocketException;

import com.stackoverflow.users.whome.Utils;

import android.app.Activity;

public class HttpServerThread extends Thread {

	String ipAddress;

	public HttpServerThread(Activity activity) throws SocketException {
		this.ipAddress = Utils.getIPAddress(true);
		if (this.ipAddress != null) {
			activity.setTitle(this.ipAddress);
		}// if
	}// a constructor

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
	}
}
