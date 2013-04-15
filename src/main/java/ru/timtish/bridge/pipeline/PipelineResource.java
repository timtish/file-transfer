package ru.timtish.bridge.pipeline;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public interface PipelineResource {

	void write(OutputStream out) throws IOException;

}
