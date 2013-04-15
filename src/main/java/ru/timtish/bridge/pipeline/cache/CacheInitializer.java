package ru.timtish.bridge.pipeline.cache;

import ru.timtish.bridge.pipeline.AbstractStream;

/**
* @author Timofey Tishin (ttishin@luxoft.com)
*/
public class CacheInitializer implements Runnable {

	private AbstractStream stream;

	public CacheInitializer(AbstractStream stream) {
		this.stream = stream;
	}

	@Override
	public void run() {
		stream.initCache();
	}
}
