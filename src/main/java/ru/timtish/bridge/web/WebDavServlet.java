package ru.timtish.bridge.web;

import org.apache.catalina.servlets.WebdavServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ru.timtish.bridge.box.StreamsBox;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.Hashtable;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
@Controller
@RequestMapping("/webdav")
public class WebDavServlet extends WebdavServlet {

	@Autowired
	private StreamsBox streamsBox;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
		Hashtable<String, String> env = new Hashtable<String, String>();
		//env.put(ProxyDirContext.HOST, this.getServletContext().getServerInfo());
		//env.put(ProxyDirContext.CONTEXT, "box");
		// todo: resources.setContext(new ProxyDirContext(env, new DirectoryContext(streamsBox.getRoot())));
	}

}
