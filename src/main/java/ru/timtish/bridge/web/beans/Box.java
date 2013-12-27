package ru.timtish.bridge.web.beans;

import ru.timtish.bridge.box.BoxEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean for box.xhtml
 */
public class Box {

    BoxEntity dir;

    public Box(BoxEntity dir) {
        this.dir = dir;
    }

    public String getName() {
        return dir.getName();
    }

    public List<File> getFiles() {
        List<File> files = new ArrayList<File>();
        for (BoxEntity stream : dir.getChilds()) files.add(new File(stream));
        return files;
    }
}
