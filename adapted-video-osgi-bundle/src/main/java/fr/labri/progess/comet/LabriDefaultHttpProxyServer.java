package fr.labri.progess.comet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Queue;

import org.littleshoot.proxy.ChainedProxy;
import org.littleshoot.proxy.ChainedProxyAdapter;
import org.littleshoot.proxy.ChainedProxyManager;
import org.littleshoot.proxy.HostResolver;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LabriDefaultHttpProxyServer implements HttpProxyServer {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(LabriDefaultHttpProxyServer.class);
	HttpProxyServer server;

	public int getIdleConnectionTimeout() {
		return server.getIdleConnectionTimeout();
	}

	public void setIdleConnectionTimeout(int idleConnectionTimeout) {
		server.setIdleConnectionTimeout(idleConnectionTimeout);
	}

	public HttpProxyServerBootstrap clone() {
		return server.clone();
	}

	public void stop() {
		server.stop();
	}

	public InetSocketAddress getListenAddress() {
		return server.getListenAddress();
	}

	public LabriDefaultHttpProxyServer() {
		InetSocketAddress addr = new InetSocketAddress("172.16.1.1", 8081);
		server = DefaultHttpProxyServer.bootstrap().withAddress(addr)
				.withChainProxyManager(new ChainedProxyManager() {

					@Override
					public void lookupChainedProxies(HttpRequest httpRequest,
							Queue<ChainedProxy> chainedProxies) {
						if (httpRequest instanceof DefaultHttpRequest) {
							DefaultHttpRequest fullreq = (DefaultHttpRequest) httpRequest;
							fullreq.headers().add("X-ADDED-HEADER", "true");
							fullreq.setUri("http://www.google.fr");
							if (httpRequest.getUri().toLowerCase()
									.contains(".fr")) {
								chainedProxies.add(new ChainedProxyAdapter() {
									@Override
									public InetSocketAddress getChainedProxyAddress() {
										return new InetSocketAddress(
												"localhost", 3128);

									}
								});
							} else {
								// chainedProxies.add(new );

							}

						}

						;

					}

				}).start();

	}
}
