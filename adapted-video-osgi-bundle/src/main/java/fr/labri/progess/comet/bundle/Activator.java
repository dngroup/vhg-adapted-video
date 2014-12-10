/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.labri.progess.comet.bundle;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.littleshoot.proxy.HttpProxyServer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.labri.progess.comet.config.LabriConfig;
import fr.labri.progess.comet.config.LabriConfigImpl;
import fr.labri.progess.comet.cron.SchedulerUtils;
import fr.labri.progess.comet.model.Content;
import fr.labri.progess.comet.model.FilterConfig;
import fr.labri.progess.comet.proxy.LabriDefaultHttpProxyServer;

public class Activator implements BundleActivator {

	static final Logger logger = LoggerFactory.getLogger(Activator.class);
	static final LabriConfig config = new LabriConfigImpl();
	HttpProxyServer server;

	public void start(BundleContext context) {
		final ConcurrentMap<String, Content> content = new ConcurrentHashMap<String, Content>();
		final Set<FilterConfig> filterConfigs = new CopyOnWriteArraySet<FilterConfig>();
		server = new LabriDefaultHttpProxyServer(config, content, filterConfigs);
		SchedulerUtils.setupScheduler(content, filterConfigs,
				config.getFrontalHostName(), config.getFrontalPort());

	}

	public void stop(BundleContext context) {
		server.stop();
	}

}