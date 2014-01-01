package ru.timtish.bridge.pipeline;

import org.springframework.core.io.InputStreamSource;
import ru.timtish.bridge.box.StreamStatus;
import ru.timtish.bridge.pipeline.cache.AutoClosableInputStream;
import ru.timtish.bridge.pipeline.cache.CachedInMemoryInputStream;
import ru.timtish.bridge.pipeline.cache.CachedInTempFileInputStream;
import ru.timtish.bridge.pipeline.cache.RepeatableInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class AbstractStream implements PipelineResource, RepeatableInputStream, Serializable {

	private static final Logger LOG = Logger.getLogger(AbstractStream.class.getName());

    String id;

	String owner;

	String name;

	String description;

	String contentType;

    transient InputStreamSource source; //todo: serialize repeatable resources
    Long sourceSize;

	AutoClosableInputStream in;

	// кеш всего потока в памяти
	RepeatableInputStream cache;

	// необходимость записывать поток несколько раз
	boolean repeatable;

	Date createdDate = new Date();

	Date lastRequest;

	int requestsCount;

	public AbstractStream(InputStreamSource source, Long size, String name, String owner, String description) {
        id = UUID.randomUUID().toString();
		this.source = source;
        this.sourceSize = size;
		this.name = name;
		this.owner = owner;
		this.description = description;
	}

	@Override
	public synchronized void write(OutputStream out) throws IOException {
		lastRequest = new Date();
		requestsCount++;

		InputStream inputStream = createCopy();

		byte[] buffer = new byte[1024];
		int size;
		while ((size = inputStream.read(buffer)) > 0 ) {
			out.write(buffer, 0, size);
		}
	}

	@Override
	public void init() {
	}

	@Override
	public synchronized void clear() {
		if (cache != null) {
			cache.clear();
			cache = null;
		}
		try {
			if (in != null) in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void initCache() {
		if (repeatable && cache == null) {
			cache = createCacheStream();
			cache.init();
		}
	}

	private RepeatableInputStream createCacheStream() {
		Long size = getSize();
		if (size != null && size < 1 * 1024 * 1024) {
			return new CachedInMemoryInputStream(getIn(), size);
		} else {
			return new CachedInTempFileInputStream(getIn(), size);
		}
	}

	@Override
	public InputStream createCopy() {
		if (repeatable) {
			if (cache == null) {
				initCache();
			}
			return cache.createCopy();
		} else {
			if (in != null && !in.isNew()) {
                if (source != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    in = null; // for reinit in getIn();
                } else {
				    throw new LoseStreamHead("Input stream already read, lose " + in.getReaded() + " bytes");
                }
			}
			return getIn();
		}
	}

	void setCache(RepeatableInputStream cache) throws IOException {
		this.cache = cache;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwner() {
		return owner;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRepeatable() {
		return repeatable;
	}

	public void setRepeatable(boolean repeatable) {
		this.repeatable = repeatable;
	}

    public Long getSize() {
        return in == null ? sourceSize : getIn().getSize();
    }

	public Date getLastRequest() {
		return lastRequest;
	}

	public void setLastRequest(Date lastRequest) {
		this.lastRequest = lastRequest;
	}

	public int getRequestsCount() {
		return requestsCount;
	}

	public void setRequestsCount(int requestsCount) {
		this.requestsCount = requestsCount;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public StreamStatus getStatus() {
        if (in == null) {
            return StreamStatus.NEW;
        } else if (in.isCompleted()) {
			if (cache instanceof CachedInMemoryInputStream) return StreamStatus.BUFFERED_TO_MEMORY;
			if (cache instanceof CachedInTempFileInputStream) return StreamStatus.BUFFERED_TO_FILE;
			return StreamStatus.CLOSED;
		} else {
			return StreamStatus.OPENED;
		}
	}

	public long getReaded() {
		return in == null ? 0 : in.getReaded();
	}

    public AutoClosableInputStream getIn() {
        if (in == null && source != null) {
            try {
                in = new AutoClosableInputStream(source.getInputStream(), sourceSize);
            } catch (IOException e) {
                // todo: add exception graph
                e.printStackTrace();
            }
        }
        return in;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
