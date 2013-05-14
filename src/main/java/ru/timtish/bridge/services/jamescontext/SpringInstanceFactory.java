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

import java.util.logging.Logger;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.logging.Log;
import org.apache.james.services.InstanceFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * {@link org.apache.james.services.InstanceFactory} implementation which use a {@link org.springframework.beans.factory.BeanFactory} to handle the loading / injection of resources
 * 
 *
 */
public class SpringInstanceFactory implements InstanceFactory, BeanFactoryAware {

	private static final Logger LOG = Logger.getLogger(SpringInstanceFactory.class.getName());

    private BeanFactory factory;

    public <T> T newInstance(Class<T> clazz) throws InstanceException {
        return newInstance(clazz, null, null);
    }

    public <T> T newInstance(Class<T> clazz, Log log, HierarchicalConfiguration config) throws InstanceException {
		try {
			return factory.getBean(clazz);
		} catch (NoSuchBeanDefinitionException e) {
			T obj = null;
			try {
				obj = clazz.newInstance();
			} catch (Exception e1) {
				throw new InstanceException("Unable create " + clazz.getName(), e1);
			}
			if (factory instanceof AutowireCapableBeanFactory) {
				((AutowireCapableBeanFactory) factory).autowireBean(obj);
				((AutowireCapableBeanFactory) factory).initializeBean(obj, clazz.getName());
			}
			return obj;
		}
    }

    public void setBeanFactory(BeanFactory factory) throws BeansException {
        this.factory = factory;
    }

}
