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
        return !isFile ? stream.getName() : ((BoxFile) stream).getInputStream().getId();
    }

    public String getDescription() {
        return stream.getDescription();
    }

    public String getSize() {
        return stream.getSize() + " b";
    }

    public int getCachePercent() {
        return !isFile || ((BoxFile) stream).getSize() == null ? 0:
                (int) (100 * ((BoxFile) stream).getInputStream().getReaded() / stream.getSize());
    }

    public String getContentType() {
        return !isFile ? null : ((BoxFile) stream).getInputStream().getContentType();
    }

    public String getContentTypeIcon() {
        return BoxUtil.getContentTypeIcon(getContentType());
    }
}
