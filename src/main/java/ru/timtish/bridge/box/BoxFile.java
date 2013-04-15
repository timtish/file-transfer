package ru.timtish.bridge.box;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.pipeline.converter.Zip;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class BoxFile implements BoxEntity {

	private BoxEntity parent;

	private String streamKey;

	transient private AbstractStream in;

	transient Boolean isContainer;

	transient List<BoxEntity> childs = Collections.emptyList();

	public BoxFile(String streamKey, BoxEntity parent) {
		this.streamKey = streamKey;
		this.parent = parent;
	}

	@Override
	public void write(OutputStream out, BoxEntityExportType type) throws IOException {
		switch (type) {
			case ZIP:
				ZipOutputStream stream = new ZipOutputStream(out);
				Zip.addToZip(getInputStream(), stream);
				break;
			case SIMPLE:
				getInputStream().write(out);
				break;
			default: throw new UnsupportedOperationException("Write directory " + type + " not supported");
		}
	}

	public String getName() {
		return getInputStream().getName();
	}

	@Override
	public String getDescription() {
		return getInputStream().getDescription();
	}

	public void setName(String name) {
		getInputStream().setName(name);
	}

	public Long getSize() {
		return getInputStream().getSize();
	}

	public AbstractStream getInputStream() {
		if (in == null) {
			in = StreamsBox.getInstance().getStream(streamKey);
		}
		return in;
	}

	public String getKey() {
		return streamKey;
	}

	public List<BoxEntity> getChilds() {
		init();
		return childs;
	}

	public BoxEntity getParent() {
		return parent;
	}

	public void setParent(BoxEntity parent) {
		this.parent = parent;
	}

	public Date getDate() {
		return getInputStream().getCreatedDate();
	}

	@Override
	public boolean isContainer() {
		init();
		return isContainer;
	}

	@Override
	public String toString() {
		return "BoxFile{" +
				"name='" + getName() + '\'' +
				", streamKey='" + streamKey + '\'' +
				", isContainer=" + isContainer +
				'}';
	}

	private void init() {
		if (isContainer != null) return;

		isContainer = false;
		try {
			if (getName().toLowerCase().endsWith(".zip")) {
				childs = Zip.getFilesTree(getInputStream().createCopy(), getKey());
				isContainer = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
