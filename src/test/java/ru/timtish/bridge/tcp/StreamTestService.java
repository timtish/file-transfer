package ru.timtish.bridge.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class StreamTestService implements Runnable {

	public static void main(String... a) {
		try {
			ServerSocket s = new ServerSocket(5555);
			while (true) {
				Socket ss = s.accept();
				new Thread(new StreamTestService(ss)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final Socket s;

	public StreamTestService(Socket s) {
		this.s = s;
	}

	@Override
	public void run() {
		try {
			System.out.print("\ns: " + s.getPort() + " " + s.getKeepAlive() + " / " + s.getSoLinger() + " " + s.getSoTimeout() + "\n");
		} catch (SocketException e) {
			e.printStackTrace();
		}
		int b;
		try {
			while ((b = s.getInputStream().read()) >= 0) {
				if ('H' == b) {
					s.getOutputStream().write("HELLO :)\n".getBytes());
				}
				System.out.print((char) b);
				if ('Y' == b) {
					s.getOutputStream().write("BAY :)\n".getBytes());

					System.out.print("\nkeepAlive: " + s.getKeepAlive() + " / " + s.getSoLinger() + " " + s.getSoTimeout() + "\n");
					if (!s.getKeepAlive()) return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
