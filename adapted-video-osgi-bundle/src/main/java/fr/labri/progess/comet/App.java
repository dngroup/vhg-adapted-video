package fr.labri.progess.comet;

import org.littleshoot.proxy.HttpProxyServer;

public class App {

	public static void main(String[] args) {
		HttpProxyServer server = new LabriDefaultHttpProxyServer();
		server.toString();
		

	}

}
