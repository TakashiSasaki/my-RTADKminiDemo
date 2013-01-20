package jp.rt_net.android.RTADKminiDemo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.res.AssetManager;

public class HttpWorkerThread extends Thread {

	static final int SOCKET_TIMEOUT = 5000;
	Socket socket;
	InputStream inputStream;
	OutputStream outputStream;
	CameraView cameraView;
	AssetManager assetManager;

	public HttpWorkerThread(Socket socket, CameraView camera_view,
			AssetManager asset_manager) throws IOException {
		this.socket = socket;
		this.socket.setSoTimeout(SOCKET_TIMEOUT);
		this.cameraView = camera_view;
		this.inputStream = this.socket.getInputStream();
		this.outputStream = this.socket.getOutputStream();
		this.assetManager = asset_manager;
	}// a constructor

	@Override
	public void run() {
		byte[] buffer = new byte[2000];
		String http_request_method;
		String http_request_path = null;
		String http_request_version;
		String[] http_request_headers;
		for (int i = 0;; i++) {
			int c;
			try {
				c = this.inputStream.read();
			} catch (IOException e) {
				e.printStackTrace();
				try {
					this.socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					return;
				}
				return;
			}// try
			if (c < 0) {
				try {
					this.socket.close();
					return;
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}// try
			}// if
			buffer[i] = (byte) c;
			if (i > 3 && buffer[i - 3] == '\r' && buffer[i - 2] == '\n'
					&& buffer[i - 1] == '\r' && buffer[i] == '\n') {
				for (int j = 0; j < i - 4; j++) {
					if (j > 2 && buffer[j - 1] == '\r' && buffer[j] == '\n') {
						Pattern COMMAND = Pattern
								.compile("^(\\w+)\\s+(.+?)\\s+HTTP/([\\d.]+)$");
						Matcher m = COMMAND
								.matcher(new String(buffer, 0, j - 1));
						if (m.matches()) {
							http_request_method = m.group(1);
							http_request_path = m.group(2);
							http_request_version = m.group(3);
						} else {
							try {
								this.socket.close();
								return;
							} catch (IOException e) {
								e.printStackTrace();
								return;
							}// try
						}// if
						http_request_headers = new String(buffer, j + 1, i - 4
								- j).split("\\r\\n");
						break;
					}// if
				}// for
				break;
			} else if (i == buffer.length - 1) {
				byte[] nbuff = new byte[buffer.length * 2];
				System.arraycopy(buffer, 0, nbuff, 0, i + 1);
				buffer = nbuff;
			}// if
		}// for

		if (http_request_path == null) {
			PrintWriter print_writer = new PrintWriter(this.outputStream);
			print_writer.print("HTTP/1.1 400 OK\r\n");
			print_writer.print("Connection: close\r\n");
			print_writer.print("Content-Length: 11\r\n");
			print_writer.print("Content-Type: text/plain\r\n\r\n");
			print_writer.print("bad request");
			print_writer.flush();
			return;
		} else if (http_request_path.equals("/hello")) {
			PrintWriter print_writer = new PrintWriter(this.outputStream);
			print_writer.print("HTTP/1.1 200 OK\r\n");
			print_writer.print("Connection: close\r\n");
			print_writer.print("Content-Length: 5\r\n");
			print_writer.print("Content-Type: text/plain\r\n\r\n");
			print_writer.print("hello");
			print_writer.flush();
			return;
		} else if (http_request_path.matches("/[0-9]+.jpg")) {
			byte[] jpeg_byte_array = this.cameraView.getLastJpegByteArray();
			if (jpeg_byte_array == null) return;
			PrintWriter print_writer = new PrintWriter(this.outputStream);
			print_writer.print("HTTP/1.1 200 OK\r\n");
			print_writer.print("Connection: close\r\n");
			print_writer.print("Content-Length: " + jpeg_byte_array.length
					+ "\r\n");
			print_writer.print("Content-Type: image/jpeg\r\n\r\n");
			print_writer.flush();
			try {
				this.outputStream.write(jpeg_byte_array);
			} catch (IOException e) {
				e.printStackTrace();
			}// try
			try {
				this.outputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}// try
			try {
				this.outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}// try
			return;
		} else if (http_request_path.equals("/camera.html")) {
			try {
				InputStream input_stream = this.assetManager
						.open("camera.html");
				ByteArrayOutputStream byte_array_output_stream = new ByteArrayOutputStream();
				while (true) {
					byte[] asset_buffer = new byte[2048];
					int count = input_stream.read(asset_buffer);
					if (count < 0)
						break;
					byte_array_output_stream.write(asset_buffer, 0, count);
				}// while
				byte[] html_byte_array = byte_array_output_stream.toByteArray();
				PrintWriter print_writer = new PrintWriter(this.outputStream);
				print_writer.print("HTTP/1.1 200 OK\r\n");
				print_writer.print("Connection: close\r\n");
				print_writer.print("Content-Length: " + html_byte_array.length
						+ "\r\n");
				print_writer.print("Content-Type: text/html\r\n\r\n");
				print_writer.flush();
				this.outputStream.write(html_byte_array);
				this.outputStream.flush();
				this.outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}// try
			return;
		}// if
	}// run
}// HttpWorkerThread
