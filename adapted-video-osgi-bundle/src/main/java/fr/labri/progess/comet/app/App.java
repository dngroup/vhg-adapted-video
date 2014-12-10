package fr.labri.progess.comet.app;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.littleshoot.proxy.HttpProxyServer;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import fr.labri.progess.comet.bundle.Activator;
import fr.labri.progess.comet.config.LabriConfig;
import fr.labri.progess.comet.cron.SchedulerUtils;
import fr.labri.progess.comet.model.FilterConfig;
import fr.labri.progess.comet.model.Content;
import fr.labri.progess.comet.proxy.LabriDefaultHttpProxyServer;

public class App {

	public static void main(String[] args) {

		try {
			LabriConfig result = CliFactory.parseArguments(LabriConfig.class,
					args);

			final ConcurrentMap<String, Content> content = new ConcurrentHashMap<String, Content>();
			final Set<FilterConfig> configs = new CopyOnWriteArraySet<FilterConfig>();

			HttpProxyServer server = new LabriDefaultHttpProxyServer(result,
					content,configs);
			server.toString();

			SchedulerUtils.setupScheduler(content,configs, result.getFrontalHostName(),
					result.getFrontalPort());
		} catch (ArgumentValidationException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

}
