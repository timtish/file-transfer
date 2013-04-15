package ru.timtish.bridge.tcp;

import java.net.Socket;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class StreamTextClient {

	private static boolean keepAlive = false;

	public static void main(String... a) {
		try {
			Socket s = null;
			for (int i = 0; i < 4; i++) {
				if (s == null || isClosed(s)) {
					System.out.print("   NEW! ");
					s = new Socket("localhost", 5555);
					s.setKeepAlive(keepAlive);
				}
				client(s);
				Thread.sleep(1000);
			}
			if (keepAlive && s != null) s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void client(Socket s) throws Exception {
		int b;
		s.getOutputStream().write("HELLO".getBytes());
		while ((b = s.getInputStream().read()) != '\n') System.out.print((char) b);
		for (int i = 0; i < 10; i++) {
			s.getOutputStream().write((" "  + i).getBytes());
			Thread.sleep(100);
		}
		s.getOutputStream().write(" BAY\n".getBytes());
		while ((b = s.getInputStream().read()) != '\n') System.out.print((char) b);

		if(!keepAlive) s.close();
	}

	private static boolean isClosed(Socket s) {
		return s.isClosed();
	}
}
