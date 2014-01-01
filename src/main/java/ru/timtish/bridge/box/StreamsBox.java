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
			removeFileByStream(root, key);
			LOG.fine("stream " + stream.getName() + " removed");
		}
	}

    private void removeFileByStream(BoxDirectory dir, String key) {
        for (BoxEntity e : dir.getChilds()) {
            if (e instanceof BoxDirectory) removeFileByStream((BoxDirectory) e, key); else
            if (e instanceof BoxFile && key.equals(((BoxFile)e).getInputStream().getId())) {
                dir.getChilds().remove(e);
                return;
            } else
            if (e instanceof BoxZipFile && key.equals(((BoxZipFile)e).getInputStream().getId())) {
                dir.getChilds().remove(e);
                return;
            }

        }
    }

    public Collection<String> getKeys() {
		return streams.keySet();
	}

	public BoxDirectory getRoot() {
		return root;
	}

    public void initRoot(BoxDirectory root) {
        this.root = root;
        addDir(root);
    }

    private void addDir(BoxDirectory dir) {
        for (BoxEntity e : dir.getChilds()) {
            if (e instanceof BoxDirectory) addDir((BoxDirectory) e); else
            if (e instanceof BoxFile) addStream(((BoxFile) e).getInputStream()); else
            if (e instanceof BoxZipFile) addStream(((BoxZipFile) e).getInputStream());

        }
    }

    private void addStream(AbstractStream stream) {
        LOG.fine("stream " + stream.getName() + " loaded");
        String key = stream.getId();
        this.streams.put(key, stream);
    }
}
