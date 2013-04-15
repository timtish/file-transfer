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
public class BoxZipFile implements BoxEntity {

	private BoxEntity parent;

	private String zipStreamKey;
	private String zipFilePath;
	private String name;

	transient private AbstractStream in;

	public BoxZipFile(String zipStreamKey, String zipFilePath, BoxEntity parent) {
		this.zipStreamKey = zipStreamKey;
		this.parent = parent;
		setZipFilePath(zipFilePath);
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
		return name;
	}

	@Override
	public String getDescription() {
		return getInputStream().getDescription();
	}

	public Long getSize() {
		return getInputStream().getSize();
	}

	public AbstractStream getInputStream() {
		if (in == null) {
			in = StreamsBox.getInstance().getStream(zipStreamKey);
		}
		return in;
	}

	public List<BoxEntity> getChilds() {
		return Collections.emptyList();
	}

	public BoxEntity getParent() {
		return parent;
	}

	public void setParent(BoxEntity parent) {
		this.parent = parent;
	}

	public String getZipStreamKey() {
		return zipStreamKey;
	}

	public void setZipStreamKey(String zipStreamKey) {
		this.zipStreamKey = zipStreamKey;
	}

	public String getZipFilePath() {
		return zipFilePath;
	}

	public void setZipFilePath(String zipFilePath) {
		if (zipFilePath != null && zipFilePath.endsWith("/")) zipFilePath = zipStreamKey.substring(0, zipFilePath.length() - 1);
		this.zipFilePath = zipFilePath;
		this.name = zipFilePath;
		if (name != null && name.contains("/")) name = name.substring(name.lastIndexOf("/") + 1);
	}

	public Date getDate() {
		return getInputStream().getCreatedDate();
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public String toString() {
		return "BoxZipFile{" +
				"name='" + getName() + '\'' +
				", streamKey='" + zipStreamKey + '\'' +
				", zipFilePath='" + zipFilePath + '\'' +
				'}';
	}
}