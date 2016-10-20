package fr.labri.progress.comet.endpoint;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.labri.progress.comet.service.StateMonitoring;

/**
 * provides access to monitoring through jax-rs rest api
 * 
 * @author dbourasseau
 *
 */
@Path("monitoring")
public class MonitoringEndpoint {

	public MonitoringEndpoint() {
		LOGGER.trace("created");

	}

	private final static Logger LOGGER = LoggerFactory.getLogger(MonitoringEndpoint.class);

	@Inject
	StateMonitoring stateMonitoringImp;

	@GET
	@Path("queuesize")
	public int getQueueSize() {
		return stateMonitoringImp.getQueueSize();

	}

}
