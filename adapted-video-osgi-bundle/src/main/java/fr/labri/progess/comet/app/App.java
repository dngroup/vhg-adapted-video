package fr.labri.progess.comet.app;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.littleshoot.proxy.HttpProxyServer;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.DateBuilder.*;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.JobBuilder.*;
import static org.quartz.ScheduleBuilder.*;
import fr.labri.progess.comet.cron.UpdateCachedContentJob;
import fr.labri.progess.comet.model.Content;
import fr.labri.progess.comet.proxy.LabriDefaultHttpProxyServer;

public class App {

	public static void main(String[] args) {

		final ConcurrentMap<String, Content> content = new ConcurrentHashMap<String, Content>();

		HttpProxyServer server = new LabriDefaultHttpProxyServer(content);
		server.toString();

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

}
