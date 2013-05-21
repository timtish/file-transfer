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

	private String zipFilePath;
	private String name;
	private Long size;

	transient private AbstractStream in;

	public BoxZipFile(AbstractStream stream, String zipFilePath, Long size, BoxEntity parent) {
		this.parent = parent;
		this.size = size;
		this.in = stream;
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
		return size;
	}

	public AbstractStream getInputStream() {
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

	public String getZipFilePath() {
		return zipFilePath;
	}

	public void setZipFilePath(String zipFilePath) {
		if (zipFilePath != null && zipFilePath.endsWith("/")) zipFilePath = zipFilePath.substring(0, zipFilePath.length() - 1);
		this.zipFilePath = zipFilePath;
		this.name = zipFilePath;
		if (name != null && name.contains("/")) name = name.substring(name.lastIndexOf("/") + 1);
		System.out.println("setZipFilePath name:" + this.name  + " zipPath:" + this.zipFilePath);
	}

	@Override
	public BoxEntity getChild(String path) {
		System.out.println("getChild name:" + zipFilePath + "/" + path);
		return new BoxZipFile(this.in, zipFilePath + "/" + path, null, parent);
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
				", zipFilePath='" + zipFilePath + '\'' +
				'}';
	}
}
