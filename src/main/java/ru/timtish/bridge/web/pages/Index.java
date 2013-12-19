package ru.timtish.bridge.web.pages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.timtish.bridge.box.StreamsBox;

/**
 * Created by ttishin on 18.12.13.
 */
@Controller
public class Index {

    @Autowired
    private StreamsBox streamsBox;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ModelAndView index() {
        System.out.println("index!");
        return new ModelAndView("WEB-INF/pages/index.jsp");
        //return "/WEB-INF/css/main.css";
        //return "/js/bridge.js";
    }

}
