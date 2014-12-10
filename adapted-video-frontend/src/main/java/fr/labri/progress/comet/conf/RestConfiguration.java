package fr.labri.progress.comet.conf;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

import fr.labri.progress.comet.endpoint.ConfigurationEndpoint;
import fr.labri.progress.comet.endpoint.ContentEndpoint;

/**
 * configure the exported resource to rest api
 * 
 * @author nherbaut
 *
 */
public class RestConfiguration extends ResourceConfig {

	public RestConfiguration() {
		// needed for spring injection
		register(RequestContextFilter.class);
		register(ContentEndpoint.class);
		register(ConfigurationEndpoint.class);
		// tell were to find resources
		// this.packages("fr.labri.progress.comet.endpoint");

	}
}