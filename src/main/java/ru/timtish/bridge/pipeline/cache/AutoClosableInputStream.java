package ru.timtish.bridge.pipeline.cache;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class AutoClosableInputStream extends InputStream {

	private Long readed;
	private Long size;
	private InputStream in;

	public AutoClosableInputStream(InputStream in, Long size) {
		this.in = in;
		this.size = size;
	}

	@Override
	public int read() throws IOException {
		if (readed == null) {
			readed = 0L;
		}
		if (in == null) {
			return -1;
		}

		int b = in.read();
		if ( b >=0 ) {
			readed++;
		}
		if (b < 0 || (size != null && readed >= size)) {
			close();
		}
		return b;
	}

	@Override
	public void close() throws IOException {
		if (in != null) {
			in.close();
			in = null;
		}
		super.close();
	}

	/**
	 * @return кол-во прочитанных байт
	 */
	public long getReaded() {
		return readed == null ? 0 : readed;
	}

	/**
	 * @return предполагаемый размер потока
	 */
	public Long getSize() {
		return size;
	}

	/**
	 * @return true если поток не читался
	 */
	public boolean isNew() {
		return readed == null;
	}

	/**
	 * @return true если поток прочитан полностью (и закрыт)
	 */
	public boolean isCompleted() {
		return in == null;
	}
}