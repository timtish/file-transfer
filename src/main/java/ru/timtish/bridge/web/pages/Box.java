package ru.timtish.bridge.web.pages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.timtish.bridge.box.BoxEntity;
import ru.timtish.bridge.box.BoxUtil;
import ru.timtish.bridge.box.StreamsBox;
import ru.timtish.bridge.web.util.UrlConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ttishin on 18.12.13.
 */
@Controller
public class Box {

    @Autowired
    private StreamsBox streamsBox;

    @RequestMapping(value = "/box",  method = RequestMethod.GET)
    public ModelAndView index(@RequestParam(value = UrlConstants.PARAM_BOX, required = false) String box,
                              @RequestParam(value = UrlConstants.PARAM_BOX_PATH, required = false) String path,
                              @RequestParam(value = UrlConstants.PARAM_NEW_KEYS, required = false) String newKeyList /* todo: Set<String>*/) {

        String user = ""; // todo: request.getRemoteUser();
        Set<String> newKeys = new HashSet<String>();
        if (newKeyList != null) {
            newKeys.addAll(Arrays.asList(newKeyList.split(",")));
        }
        int i = 1;
        BoxEntity dir = BoxUtil.getBoxEntity(user, box, path);
        if (dir == null) dir = streamsBox.getRoot();

        return new ModelAndView("WEB-INF/pages/box.xhtml", Collections.singletonMap("box", new ru.timtish.bridge.web.beans.Box(dir)));
    }

}
