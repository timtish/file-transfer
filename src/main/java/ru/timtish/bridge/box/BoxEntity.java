package ru.timtish.bridge.box;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public interface BoxEntity extends Serializable {

	/**
	 * @return имя объекта (без пути к нему)
	 */
	String getName();

	/**
	 * @return дата изменения
 	 */
	Date getDate();

	String getDescription();

	/**
	 * @return размер файла
	 */
	Long getSize();

	void write(OutputStream out, BoxEntityExportType type) throws IOException;

	List<BoxEntity> getChilds();

	BoxEntity getChild(String path);

	BoxEntity getParent();

	boolean isContainer();
}
