package ru.timtish.bridge.pipeline.cache;

import java.io.*;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class CachedInTempFileInputStream extends AutoClosableInputStream implements RepeatableInputStream {

	private OutputStream out;
	private File cache;
	private boolean completed;

	public CachedInTempFileInputStream(InputStream in, Long size) {
		super(in, size);
	}

	@Override
	public int read() throws IOException {
		boolean isNew = isNew();
		int b = super.read();
		if (!completed) {
			if ( b >= 0) {
				if (out == null) {
					if (!isNew) {
						throw new IllegalStateException("Input stream already read, but not saved to cache");
					}
					cache = File.createTempFile("CachedInTempFileInputStream", "");
					out = new FileOutputStream(cache);
				}
				out.write(b);
			}
			if (out != null && (b < 0 || (getSize() != null && getReaded() >= getSize()))) {
				out.close();
				out = null;
				completed = true;
			}
		}
		return b;
	}

	@Override
	public InputStream createCopy() {
		if (!completed) {
			init();
		}
		try {
			return new FileInputStream(cache);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Cache tmp file not found", e);
		}
	}


	@Override
	public void init() {
		try {
			while (read() >= 0);
		} catch (IOException e) {
			throw new IllegalStateException("Can't init cache", e);
		}
	}

	@Override
	public void clear() {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			out = null;
		}
		if (cache != null) {
			cache.delete();
			cache = null;
		}
	}
}
