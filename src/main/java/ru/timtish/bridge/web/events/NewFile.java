package ru.timtish.bridge.web.events;

/**
 * Created by ttishin on 25.12.13.
 */
public class NewFile {

    private String key;

    public NewFile() {
    }

    public NewFile(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "NewFile{" +
                "key='" + key + '\'' +
                '}';
    }
}
