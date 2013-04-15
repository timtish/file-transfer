package ru.timtish.bridge.pipeline.cache;

import java.io.InputStream;

/**
 * Кланируемый поток.
 *
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public interface RepeatableInputStream {

	void init();
	/**
	 * @return копия потока
	 */
	InputStream createCopy();

	void clear();

}
