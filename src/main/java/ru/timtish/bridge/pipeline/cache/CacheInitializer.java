package ru.timtish.bridge.pipeline.cache;

import ru.timtish.bridge.pipeline.AbstractStream;

/**
* @author Timofey Tishin (ttishin@luxoft.com)
*/
public class CacheInitializer implements Runnable {

    public static enum CacheType {OFF, IN_NEW_THREAD, FULL}

	private AbstractStream stream;

	public CacheInitializer(AbstractStream stream) {
		this.stream = stream;
	}

	@Override
	public void run() {
		stream.initCache();
	}

    public static void init(AbstractStream stream, CacheType type) {
        switch (type) {
            case OFF:
                return;

            case FULL:
                new CacheInitializer(stream).run();
                return;

            case IN_NEW_THREAD:
                new Thread(new CacheInitializer(stream)).start();

        }
    }
}
