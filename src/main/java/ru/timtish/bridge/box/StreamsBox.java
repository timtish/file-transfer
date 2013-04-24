package ru.timtish.bridge.box;

import java.util.*;
import java.util.logging.Logger;

import ru.timtish.bridge.pipeline.AbstractStream;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class StreamsBox {

	private static final Logger LOG = Logger.getLogger(StreamsBox.class.getName());

	private static final StreamsBox INSTANCE = new StreamsBox(new BoxDirectory("root", new Date(), new ArrayList<BoxEntity>(), null));

	public static StreamsBox getInstance() {
		return INSTANCE;
	}

	protected StreamsBox(BoxDirectory root) {
		this.root = root;
	}

	private Map<String, AbstractStream> streams = new HashMap<String, AbstractStream>();

	private BoxDirectory root;

	public AbstractStream getStream(String key) {
		return streams.get(key);
	}

	public void addStreams(String key, AbstractStream stream) {
		LOG.fine("stream " + stream.getName() + " linked");
		this.streams.put(key, stream);
		// todo: check security
		root.getChilds().add(new BoxFile(key, root));
	}

	public void release(String key) {
		AbstractStream stream = streams.get(key);
		if (stream != null) {
			stream.clear();
			streams.remove(key);
			// todo: root.getChilds().remove()
			LOG.fine("stream " + stream.getName() + " removed");
		}
	}

	public Collection<String> getKeys() {
		return streams.keySet();
	}

	public BoxDirectory getRoot() {
		return root;
	}
}
