package ru.timtish.bridge.pipeline.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class CachedInMemoryInputStream extends AutoClosableInputStream implements RepeatableInputStream {

	private ByteArrayOutputStream out;
	private byte[] cache;

	public CachedInMemoryInputStream(InputStream in, Long size) {
		super(in, size);
	}

	@Override
	public int read() throws IOException {
		boolean isNew = isNew();
		int b = super.read();
		if (cache == null) {
			if ( b >= 0) {
				if (out == null) {
					if (!isNew) {
						throw new IllegalStateException("Input stream already read, but not saved to cache");
					}
					out = new ByteArrayOutputStream();
				}
				out.write(b);
			}
			if (out != null && (b < 0 || (getSize() != null && getReaded() >= getSize()))) {
				cache = out.toByteArray();
				System.out.println("out. " + out.size() + " = " +  out.toByteArray().length + " " + cache.length);
				out.close();
				out = null;
			}
		}
		return b;
	}

	@Override
	public InputStream createCopy() {
		if (cache == null) {
			init();
			// todo: return Concat Streams
		}
		return new ByteArrayInputStream(cache);
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
		cache = null;
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			out = null;
		}
	}
}
