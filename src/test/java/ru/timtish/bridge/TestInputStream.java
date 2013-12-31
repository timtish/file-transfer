package ru.timtish.bridge;

import org.springframework.core.io.InputStreamSource;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class TestInputStream extends InputStream implements InputStreamSource {

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

    @Override
    public InputStream getInputStream() throws IOException {
        return this;
    }
}
