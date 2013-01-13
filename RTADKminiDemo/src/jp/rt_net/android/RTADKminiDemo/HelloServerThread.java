package jp.rt_net.android.RTADKminiDemo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpException;

import com.stackoverflow.users.whome.Utils;

import android.app.Activity;

public class HelloServerThread extends Thread {

	String ipAddress;
	ServerSocket serverSocket;
	boolean inProcess;

	public HelloServerThread() throws IOException {
		this.serverSocket = new ServerSocket();
		this.serverSocket.setReuseAddress(true);
		this.serverSocket.bind(new InetSocketAddress(12345));
	}// a constructor

	@Override
	public void run() {
		while (true) {
			InputStream input_stream = null;
			Socket socket = null;
			try {
				socket = this.serverSocket.accept();
				input_stream = socket.getInputStream();
			} catch (IOException e) {
				if (!this.inProcess)
					return;
			}// try

			byte[] buffer = new byte[2000];
			String http_request_method;
			String http_request_path;
			String http_request_version;
			String[] http_request_headers;
			for (int i = 0;; i++) {
				int c;
				try {
					c = input_stream.read();
				} catch (IOException e) {
					e.printStackTrace();
					try {
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
						return;
					}
					return;
				}// try
				if (c < 0) {
					try {
						socket.close();
						return;
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}// try
				}// if
				buffer[i] = (byte) c;
				if (i > 3 && buffer[i - 3] == '\r' && buffer[i - 2] == '\n'
						&& buffer[i - 1] == '\r' && buffer[i] == '\n') {
					for (int j = 0; i < i - 4; j++) {
						if (j > 2 && buffer[j - 1] == '\r' && buffer[j] == '\n') {
							Pattern COMMAND = Pattern
									.compile("^(\\w+)\\s+(.+?)\\s+HTTP/([\\d.]+)$");
							Matcher m = COMMAND.matcher(new String(buffer, 0,
									j - 1));
							if (m.matches()) {
								http_request_method = m.group(1);
								http_request_path = m.group(2);
								http_request_version = m.group(3);
							} else {
								try {
									socket.close();
									return;
								} catch (IOException e) {
									e.printStackTrace();
									return;
								}// try
							}
							http_request_headers = new String(buffer, j + 1, i
									- 4 - j).split("\\r\\n");
							break;
						}
					}
					break;
				} else if (i == buffer.length - 1) {
					byte[] nbuff = new byte[buffer.length * 2];
					System.arraycopy(buffer, 0, nbuff, 0, i + 1);
					buffer = nbuff;
				}// if
			}// for

			OutputStream output_stream;
			try {
				output_stream = socket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}// try
			PrintWriter print_writer = new PrintWriter(output_stream);
			print_writer.print("HTTP/1.1 200 OK\r\n");
			print_writer.print("Connection: close\r\n");
			print_writer.print("Content-Length: 5\r\n");
			print_writer.print("Content-Type: text/plain\r\n\r\n");
			print_writer.print("hello");
			print_writer.flush();
		}// while
	}// run

	public void stopServer() throws IOException {
		if (this.serverSocket != null) {
			this.inProcess = false;
			this.serverSocket.close();
		}
	}
}// HttpServerThread
