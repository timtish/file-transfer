package ru.timtish.bridge.pipeline;

/**
 * Created by ttishin on 25.12.13.
 */
public class LoseStreamHead extends IllegalStateException {

    public LoseStreamHead() {
    }

    public LoseStreamHead(String s) {
        super(s);
    }
}
