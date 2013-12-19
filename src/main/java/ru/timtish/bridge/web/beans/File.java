package ru.timtish.bridge.web.beans;

import ru.timtish.bridge.box.BoxEntity;
import ru.timtish.bridge.box.BoxFile;
import ru.timtish.bridge.box.BoxUtil;

/**
 * File from box file list.
 */
public class File {

    boolean isFile;
    BoxEntity stream;

    public File(BoxEntity stream) {
        this.stream = stream;
        isFile = stream instanceof BoxFile;
    }

    public String getName() {
        return stream.getName();
    }

    public String getKey() {
        return ""; // todo: isFile ? BoxUtil.findStreamKey(streamsBox, ((BoxFile) stream).getInputStream()) : null;
    }

    public String getId() {
        return stream.getName();
    }

    public String getAddress() {
        return stream.getName();
    }


    public String getDescription() {
        return stream.getDescription();
    }


    public String getSize() {
        return stream.getSize() + " b";
    }

    public String getBoxId() {
        return BoxUtil.getId(stream);
    }
}
