/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/
package ru.timtish.bridge.services.jamescontext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.apache.james.services.FileSystem;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;

public class SpringFileSystem implements FileSystem, ApplicationContextAware {

    private ApplicationContext context = null;

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}

	private ServletContext getContext() {
		return ((AbstractRefreshableWebApplicationContext) context).getServletContext();
	}

    public File getBasedir() throws FileNotFoundException {
		return new File(getContext().getRealPath("/"));
    }

    public InputStream getResource(String url) throws IOException {
        return getContext().getResourceAsStream(url);
    }

    public File getFile(String fileURL) throws FileNotFoundException {
		File file = new File(getContext().getRealPath(fileURL));
        if (!file.exists()) {
            throw new FileNotFoundException("Not exists file " + fileURL);
        }
		return file;
    }

}
