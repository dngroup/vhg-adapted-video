package fr.labri.progess.comet.app;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.littleshoot.proxy.HttpProxyServer;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import fr.labri.progess.comet.bundle.Activator;
import fr.labri.progess.comet.cron.SchedulerUtils;
import fr.labri.progess.comet.model.Content;
import fr.labri.progess.comet.proxy.LabriDefaultHttpProxyServer;

public class App {

	interface Config {
		@Option(defaultValue = "localhost", helpRequest = true)
		String getHostName();

		@Option(defaultValue = "8084", helpRequest = true)
		Integer getPort();
	}

	public static void main(String[] args) {

		try {
			Config result = CliFactory.parseArguments(Config.class, args);

			final ConcurrentMap<String, Content> content = new ConcurrentHashMap<String, Content>();

			HttpProxyServer server = new LabriDefaultHttpProxyServer(
					result.getHostName(), result.getPort(), content);
			server.toString();

			SchedulerUtils.setupScheduler(content);
		} catch (ArgumentValidationException e) {
			System.out.println(e.getMessage());
		}

	}

}
