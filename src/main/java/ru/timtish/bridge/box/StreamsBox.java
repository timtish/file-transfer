package ru.timtish.bridge.box;

import java.util.*;
import java.util.logging.Logger;

import ru.timtish.bridge.pipeline.AbstractStream;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class StreamsBox {

	private static final Logger LOG = Logger.getLogger(StreamsBox.class.getName());

	private static final StreamsBox INSTANCE = new StreamsBox();

	public static StreamsBox getInstance() {
		return INSTANCE;
	}

	private Map<String, AbstractStream> streams = new HashMap<String, AbstractStream>();

	private List<BoxDirectory> dirs = new ArrayList<BoxDirectory>();

	private BoxDirectory defaultDir = new BoxDirectory("root", new Date(), new ArrayList<BoxEntity>(), null);

	public AbstractStream getStream(String key) {
		return streams.get(key);
	}

	public void addStreams(String key, AbstractStream stream) {
		LOG.fine("stream " + stream.getName() + " linked");
		this.streams.put(key, stream);
		// todo: check security
		defaultDir.getChilds().add(new BoxFile(key, defaultDir));
	}

	public void release(String key) {
		AbstractStream stream = streams.get(key);
		if (stream != null) {
			stream.clear();
			streams.remove(key);
			// todo: defaultDir.getChilds().remove()
			LOG.fine("stream " + stream.getName() + " removed");
		}
	}

	public Collection<String> getKeys() {
		return streams.keySet();
	}

	public BoxEntity getBoxEntity(String user, String path) {
		// todo: check security, find user directory
		return BoxUtil.findEntity(defaultDir, path);
	}

}
