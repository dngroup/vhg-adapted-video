package fr.labri.progress.comet.app;

import java.io.IOException;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.servlet.ServletContainer;
import org.hsqldb.server.Server;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import com.lexicalscope.jewel.cli.InvalidOptionSpecificationException;
import com.lexicalscope.jewel.cli.Option;

import fr.labri.progress.comet.conf.CliConfSingleton;
import fr.labri.progress.comet.conf.SpringConfiguration;

/**
 * Main class.
 *
 */
public class Main {
	// Base URI the Grizzly HTTP server will listen on

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
//salut
		try {
			CliConfiguration cliconf = CliFactory.parseArguments(
					CliConfiguration.class, args);

			CliConfSingleton.rabbitHost = cliconf.getRabbitHost();
			CliConfSingleton.rabbitUser = cliconf.getRabbitUser();
			CliConfSingleton.rabbitPassword = cliconf.getRabbitPassword();
			CliConfSingleton.rabbitPort = cliconf.getRabbitPort();
			CliConfSingleton.streamerHost = cliconf.getStreamerHost();
			CliConfSingleton.streamerPort = cliconf.getStreamerPort();

			String baseHost = cliconf.getHost();
			int pasePort = cliconf.getPort();

			// WEB APP SETUP

			// instead of using web.xml, we use java-based configuration
			WebappContext webappContext = new WebappContext("production");

			// add a listener to spring so that IoC can happen
			webappContext.addListener(ContextLoaderListener.class);

			// specify that spring should be configured with annotations
			webappContext.addContextInitParameter(
					ContextLoader.CONTEXT_CLASS_PARAM,
					AnnotationConfigWebApplicationContext.class.getName());

			// and where spring should find its configuration
			webappContext.addContextInitParameter(
					ContextLoader.CONFIG_LOCATION_PARAM,
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
			NetworkListener listener = new NetworkListener("grizzly2",
					baseHost, pasePort);
			server.addListener(listener);

			// finally, deploy the webapp
			webappContext.deploy(server);
			server.start();

			System.out.println(String
					.format("Jersey app started with WADL available at http://"
							+ baseHost + ":" + pasePort + "/" + BASE_PATH
							+ "/application.wadl"));

			// DATABASE SETUP

			Server dbServer = new Server();

			dbServer.setDatabaseName(0, "cache-orchestrator");
			dbServer.setDatabasePath(0, "mem:cache-orchestrator");
			dbServer.setDaemon(true);
			dbServer.start();

			// wait for the server to die before we quit
			Thread.currentThread().join();
		} catch (HelpRequestedException ios) {
			System.out.println(ios.getMessage());
		}
	}
}

interface CliConfiguration {

	@Option(shortName = "p", longName = "port", defaultValue = "8080", description = "the port on which the frontend will listen for http connections")
	Integer getPort();

	@Option(shortName = "h", longName = "host", defaultValue = "localhost", description = "the hostname or IP on which the frontend will listen for http connections")
	String getHost();

	@Option(longName = "rabbit-host", defaultValue = "localhost")
	String getRabbitHost();

	@Option(longName = "rabbit-user", defaultValue = "guest")
	String getRabbitUser();

	@Option(longName = "rabbit-password", defaultValue = "guest")
	String getRabbitPassword();

	@Option(longName = "rabbit-port", defaultValue = "5672")
	Integer getRabbitPort();

	@Option(longName = "streamer-port", defaultValue = "80", description = "the port on which the streamer will stream its data")
	Integer getStreamerPort();

	@Option(longName = "streamer-host", defaultValue = "streamer", description = "the host on which the streamer will stream its data")
	String getStreamerHost();

	@Option(helpRequest = true)
	boolean getHelp();
}