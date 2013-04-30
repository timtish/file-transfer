package ru.timtish.bridge.web;

import java.util.Hashtable;

import javax.servlet.ServletException;

import org.apache.catalina.servlets.WebdavServlet;
import org.apache.naming.resources.ProxyDirContext;
import org.springframework.beans.factory.annotation.Autowired;
import ru.timtish.bridge.box.StreamsBox;
import ru.timtish.bridge.web.util.DirectoryContext;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class WebDavServlet extends WebdavServlet {

	@Autowired
	private StreamsBox streamsBox;

	@Override
	public void init() throws ServletException {
		super.init();
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(ProxyDirContext.HOST, this.getServletContext().getServerInfo());
		env.put(ProxyDirContext.CONTEXT, "box");
		resources = new ProxyDirContext(env, new DirectoryContext(streamsBox.getRoot()));
	}

}
