package fr.labri.progess.comet.cron;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

public abstract class FrontalJob implements Job {

	protected static Client client = ClientBuilder.newClient();

	protected String getFrontalConnection(JobExecutionContext context) {
		final String frontalHost = (String) context.getJobDetail()
				.getJobDataMap().get("frontalHost");
		final Integer frontalPort = (Integer) context.getJobDetail()
				.getJobDataMap().get("frontalPort");
		final String frontalUrl = ("http://" + frontalHost + ":" + frontalPort);
		return frontalUrl;
	}

}