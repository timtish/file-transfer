package ru.timtish.bridge;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class TestOutputStream extends OutputStream {

	long i;

	@Override
	public void write(int b) throws IOException {
		if ((i++ % 256) != (b & 0xFF)) throw new IllegalArgumentException("Expect " + i + " actual write " + b);
	}
}
