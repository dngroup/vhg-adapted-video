package fr.labri.progess.comet.cron;

import java.util.Set;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.labri.progess.comet.model.FilterConfig;
import fr.labri.progess.comet.model.FilterConfigWrapper;

public class UpdateConfigJob extends FrontalJob {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UpdateConfigJob.class);

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		String frontalUrl = this.getFrontalConnection(context);
		try {
			@SuppressWarnings("unchecked")
			final Set<FilterConfig> filterConfigs = (Set<FilterConfig>) context
					.getJobDetail().getJobDataMap().get("filter-config");

			WebTarget target = client.target(frontalUrl).path("api")
					.path("config").path("dummy");
			FilterConfigWrapper wrapper = target.request(
					MediaType.APPLICATION_XML).get(FilterConfigWrapper.class);

			filterConfigs.clear();
			filterConfigs.addAll(wrapper.getFilterConfigs());

		} catch (ProcessingException e) {
			LOGGER.warn(
					"failed to retreive config from frontend on {}, I will retry later",
					frontalUrl);
		}
	}

}
