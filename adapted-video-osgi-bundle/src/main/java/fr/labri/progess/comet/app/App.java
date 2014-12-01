package fr.labri.progess.comet.app;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.littleshoot.proxy.HttpProxyServer;

import fr.labri.progess.comet.bundle.Activator;
import fr.labri.progess.comet.model.Content;
import fr.labri.progess.comet.proxy.LabriDefaultHttpProxyServer;

public class App {

	public static void main(String[] args) {

		final ConcurrentMap<String, Content> content = new ConcurrentHashMap<String, Content>();

		HttpProxyServer server = new LabriDefaultHttpProxyServer(content);
		server.toString();
		
		Activator.setupScheduler(content);

	

	}

}
