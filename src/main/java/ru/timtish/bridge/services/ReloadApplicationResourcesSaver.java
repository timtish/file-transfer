package ru.timtish.bridge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;
import ru.timtish.bridge.box.BoxDirectory;
import ru.timtish.bridge.box.StreamsBox;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;

public class ReloadApplicationResourcesSaver implements ServletContextListener {

    public static final String TEMP_FILE_NAME = "bridge_reload_info.bin";

    @Autowired
    StreamsBox streamsBox;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
        WebApplicationContextUtils
                .getRequiredWebApplicationContext(sce.getServletContext())
                .getAutowireCapableBeanFactory()
                .autowireBean(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        File f = new File(TEMP_FILE_NAME);

        if (streamsBox == null || !f.exists() || !f.isFile()) {
            return;
        }

        ObjectInputStream reader = null;
        try {
            reader = new ObjectInputStream(new FileInputStream(f));
            streamsBox.initRoot((BoxDirectory) reader.readObject());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) try {
                reader.close();
                f.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ObjectOutputStream writer = null;
        try {
            writer = new ObjectOutputStream(new FileOutputStream(TEMP_FILE_NAME));
            writer.writeObject(streamsBox.getRoot());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
