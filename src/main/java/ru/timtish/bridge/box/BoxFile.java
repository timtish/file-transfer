package ru.timtish.bridge.box;

import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.pipeline.converter.Zip;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class BoxFile implements BoxEntity {

	private BoxEntity parent;

    private AbstractStream in;

	private Boolean isContainer;

	private List<BoxEntity> childs = Collections.emptyList();

    public BoxFile() {
    }

    public BoxFile(AbstractStream stream, BoxEntity parent) {
		this.in = stream;
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
		return in;
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
	public BoxEntity getChild(String fileName) {

		System.out.println("f.findEntity: " + fileName + " in " + getName());

		if (fileName == null) return this;
		if (fileName.startsWith(File.separator)) fileName = fileName.substring(1);
		if (fileName.endsWith(File.separator)) fileName = fileName.substring(0, fileName.length() - 1);
		if (fileName.isEmpty()) return this;

		for (BoxEntity entity : getChilds()) {

			System.out.println("f.findEntity: child: " + entity.getName());
			if (fileName.equals(entity.getName())) return entity;
			if (entity.isContainer() && fileName.startsWith(entity.getName() + File.separatorChar)) {
				return entity.getChild(fileName.substring(entity.getName().length() + 1));
			}
		}
		System.out.println("f.findEntity: not found");

		return null;
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
				", isContainer=" + isContainer +
				'}';
	}

	private void init() {
		if (isContainer != null) return;

		isContainer = false;
		try {
			if (getName().toLowerCase().endsWith(".zip")) {
				childs = Zip.getFilesTree(getInputStream());
				isContainer = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
