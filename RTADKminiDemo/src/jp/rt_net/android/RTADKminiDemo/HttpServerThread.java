package jp.rt_net.android.RTADKminiDemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.res.AssetManager;

public class HttpServerThread extends Thread {

	final int HTTP_PORT = 12346;
	String ipAddress;
	ServerSocket serverSocket;
	boolean inProcess;
	CameraView cameraView;
	AssetManager assetManager;

	public HttpServerThread(CameraView camera_view, AssetManager asset_manager)
			throws IOException {
		this.assetManager = asset_manager;
		this.cameraView = camera_view;
		this.serverSocket = new ServerSocket();
		this.serverSocket.setReuseAddress(true);
		this.serverSocket.bind(new InetSocketAddress(this.HTTP_PORT));
	}// a constructor

	@Override
	public void run() {
		while (true) {
			try {
				Socket socket = this.serverSocket.accept();
				new HttpWorkerThread(socket, this.cameraView, this.assetManager)
						.start();
			} catch (IOException e) {
				if (!this.inProcess)
					return;
			}// try
		}// while
	}// run

	public void stopServer() throws IOException {
		if (this.serverSocket != null) {
			this.inProcess = false;
			this.serverSocket.close();
		}// if
	}// stopServer
}// HttpServerThread
