package ru.timtish.bridge;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class TestInputStream extends InputStream {

	long i;

	long size;

	public TestInputStream(long size) {
		this.size = size;
	}

	@Override
	public int read() throws IOException {
		if (i < size) {
			return (int) (i++ % 256);
		} else {
			return -1;
		}
	}

	@Override
	public synchronized void reset() throws IOException {
		i = 0;
	}
}
