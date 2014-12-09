package fr.labri.progess.comet.cron;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.labri.progess.comet.model.Content;

public abstract class SchedulerUtils {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SchedulerUtils.class);

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
			LOGGER.error("failed to start Scheduler", e);
			throw new RuntimeException(e);

		}
	}
}
