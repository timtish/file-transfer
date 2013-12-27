package ru.timtish.bridge.box;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.services.SessionManager;
import ru.timtish.bridge.web.events.NewFile;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
@Component("streamsBox")
public class StreamsBox {

	private static final Logger LOG = Logger.getLogger(StreamsBox.class.getName());

    @Autowired
    SessionManager sessionManager;


	private Map<String, AbstractStream> streams = new HashMap<String, AbstractStream>();

	private BoxDirectory root;

    public StreamsBox() {
        this(new BoxDirectory("root", new Date(), new ArrayList<BoxEntity>(), null));
    }

    protected StreamsBox(BoxDirectory root) {
        this.root = root;
    }

	public AbstractStream getStream(String key) {
		return streams.get(key);
	}

	public String addStreams(AbstractStream stream) {
		LOG.fine("stream " + stream.getName() + " linked");
        String key = stream.getId();
		this.streams.put(key, stream);
		// todo: check security
		root.getChilds().add(new BoxFile(stream, root));
        sessionManager.send(root.getName(), new NewFile(key));
        return key;
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
