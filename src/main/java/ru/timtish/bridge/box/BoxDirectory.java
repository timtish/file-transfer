package ru.timtish.bridge.box;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ru.timtish.bridge.pipeline.converter.Zip;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class BoxDirectory implements BoxEntity {

	private String name;

	private Date date;

	private Long size;

	private List<BoxEntity> childs;

	private BoxEntity parent;

	public BoxDirectory(String name, Date date, List<BoxEntity> childs, BoxEntity parent) {
		this.name = name;
		this.date = date;
		this.childs = childs;
		this.parent = parent;
	}

	@Override
	public void write(OutputStream out, BoxEntityExportType type) throws IOException {
		switch (type) {
			case ZIP:
				ZipOutputStream stream = new ZipOutputStream(out);
				addChilds(stream, childs);
				break;
			default: throw new UnsupportedOperationException("Write directory " + type + " not supported");
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSize() {
		if (size == null) {
			size = 0L;
			if (getChilds() != null) {
				for (BoxEntity child : getChilds()) {
					Long childSize = child.getSize();
					if (childSize != null) size += childSize;
				}
			}
		}
		return size;
	}

	private static void addChilds(ZipOutputStream stream, Collection<BoxEntity> childs) throws IOException {
		for (BoxEntity child : childs) {
			if (child.isContainer()) {
				addFolder((BoxDirectory) child, stream);
			} else {
				Zip.addToZip(((BoxFile) child).getInputStream(), stream);
			}
		}
	}

	private static void addFolder(BoxDirectory dir, ZipOutputStream stream) throws IOException {
		ZipEntry zipEntry = new ZipEntry(dir.getName() + "/");
		stream.putNextEntry(zipEntry);
		addChilds(stream, dir.getChilds());
		stream.closeEntry();
	}

	public BoxEntity getChild(String fileName) {

		System.out.println("findEntity: " + fileName + " in " + getName());

		if (fileName == null) return this;
		if (fileName.startsWith("/")) fileName = fileName.substring(1);
		if (fileName.endsWith("/")) fileName = fileName.substring(0, fileName.length() - 1);
		if (fileName.isEmpty()) return this;

		for (BoxEntity entity : getChilds()) {

			System.out.println("findEntity: child: " + entity.getName());
			if (fileName.equals(entity.getName())) return entity;
			if (entity.isContainer() && fileName.startsWith(entity.getName() + "/")) {
				return entity.getChild(fileName.substring(entity.getName().length() + 1));
			}
		}
		System.out.println("findEntity: not found");

		return null;
	}


	public List<BoxEntity> getChilds() {
		return childs;
	}

	public void setChilds(List<BoxEntity> childs) {
		this.childs = childs;
	}

	public BoxEntity getParent() {
		return parent;
	}

	public void setParent(BoxEntity parent) {
		this.parent = parent;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	@Override
	public String toString() {
		return "BoxDirectory{" +
				"name='" + name + '\'' +
				'}';
	}
}
