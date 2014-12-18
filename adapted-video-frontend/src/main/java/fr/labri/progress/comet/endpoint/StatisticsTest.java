package fr.labri.progress.comet.endpoint;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.glassfish.jersey.server.monitoring.MonitoringStatistics;

@Path("stats")
public class StatisticsTest {
	@Inject
	Provider<MonitoringStatistics> statistics;

	@GET
	public long getTotalExceptionMappings() throws InterruptedException {
		final MonitoringStatistics monitoringStatistics = statistics.get();
		final long totalExceptionMappings = monitoringStatistics
				.getExceptionMapperStatistics().getTotalMappings();

		return totalExceptionMappings;
	}
}