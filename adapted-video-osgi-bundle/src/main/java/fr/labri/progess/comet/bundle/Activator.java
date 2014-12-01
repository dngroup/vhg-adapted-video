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

import static org.quartz.DateBuilder.evenSecondDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.littleshoot.proxy.HttpProxyServer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.labri.progess.comet.cron.UpdateCachedContentJob;
import fr.labri.progess.comet.model.Content;
import fr.labri.progess.comet.proxy.LabriDefaultHttpProxyServer;

public class Activator implements BundleActivator {

	final ConcurrentMap<String, Content> content = new ConcurrentHashMap<String, Content>();

	Logger logger = LoggerFactory.getLogger(Activator.class);
	HttpProxyServer server;

	public void start(BundleContext context) {
		server = new LabriDefaultHttpProxyServer(content);

		try {
			SchedulerFactory sf = new StdSchedulerFactory();

			Scheduler sched = sf.getScheduler();

			JobDetail job = newJob(UpdateCachedContentJob.class).withIdentity(
					"job1", "group1").build();
			job.getJobDataMap().put("content-cache", content);

			Date runTime = evenSecondDate(new Date());
			Trigger trigger = newTrigger().withIdentity("trigger1", "group1")
					.startAt(runTime).build();
			sched.scheduleJob(job, trigger);

			sched.start();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void setupScheduler(ConcurrentMap<String, Content> content) {
		try {
			StdSchedulerFactory sf = new StdSchedulerFactory();
			Properties props = new Properties();
			props.put("org.quartz.threadPool.threadCount", "1");
			sf.initialize(props);

			Scheduler sched = sf.getScheduler();

			JobDetail job = newJob(UpdateCachedContentJob.class).withIdentity(
					"job1", "group1").build();
			job.getJobDataMap().put("content-cache", content);

			Trigger trigger = newTrigger()
					.withSchedule(
							simpleSchedule().withIntervalInSeconds(10)
									.repeatForever()).startAt(new Date())
					.build();

			sched.scheduleJob(job, trigger);

			sched.start();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stop(BundleContext context) {
		server.stop();
	}

}