package fr.labri.progress.comet.app;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import fr.labri.progress.comet.conf.SpringConfiguration;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.Application;

/**
 * Main class.
 *
 */
public class Main {
	// Base URI the Grizzly HTTP server will listen on

	public static final String BASE_HOST = "localhost";
	public static final int BASE_PORT = 8082;
	public static final String BASE_PATH = "api";

	/**
	 * Main method.
	 * 
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {

		// instead of using web.xml, we use java-based configuration
		WebappContext webappContext = new WebappContext("production");

		// add a listener to spring so that IoC can happen
		webappContext.addListener(ContextLoaderListener.class);

		// specify that spring should be configured with annotations
		webappContext.addContextInitParameter(
				ContextLoader.CONTEXT_CLASS_PARAM,
				AnnotationConfigWebApplicationContext.class.getName());

		// and where spring should find its configuration
		webappContext
				.addContextInitParameter(ContextLoader.CONFIG_LOCATION_PARAM,
						SpringConfiguration.class.getName());
		// attache the jersey servlet to this context
		ServletRegistration jerseyServlet = webappContext.addServlet(
				"jersey-servlet", ServletContainer.class);

		// configure it with extern configuration class
		jerseyServlet.setInitParameter("javax.ws.rs.Application",
				fr.labri.progress.comet.conf.RestConfiguration.class
						.getName());

		// finally, map it to the path
		jerseyServlet.addMapping("/" + BASE_PATH + "/*");

		// start a vanilla server
		HttpServer server = new HttpServer();

		// configure a network listener with our configuration
		NetworkListener listener = new NetworkListener("grizzly2", BASE_HOST,
				BASE_PORT);
		server.addListener(listener);

		// finally, deploy the webapp
		webappContext.deploy(server);
		server.start();

		System.out.println(String
				.format("Jersey app started with WADL available at http://"
						+ BASE_HOST + ":" + BASE_PORT + "/" + BASE_PATH
						+ "/application.wadl"));

		// wait for the server to die before we quit
		Thread.currentThread().join();
	}
}
