package fr.labri.progess.comet.proxy;

import fr.labri.progess.comet.model.Content;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentMap;

import org.littleshoot.proxy.ChainedProxy;
import org.littleshoot.proxy.ChainedProxyAdapter;
import org.littleshoot.proxy.ChainedProxyManager;
import org.littleshoot.proxy.HostResolver;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSource;
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
	protected ConcurrentMap<String, Content> content;
	final private CacheService cacheService = new CacheService();

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

	public LabriDefaultHttpProxyServer(
			final ConcurrentMap<String, Content> content) {
		this.content = content;
		LOGGER.debug("content is {}",this.content);
		InetSocketAddress addr = new InetSocketAddress("172.16.1.1", 8084);
		server = DefaultHttpProxyServer.bootstrap().withAddress(addr)
				.withFiltersSource(new HttpFiltersSource() {

					public HttpFilters filterRequest(HttpRequest originalRequest) {
						return new HttpFiltersAdapter(originalRequest, null) {
							@Override
							public HttpResponse clientToProxyRequest(
									HttpObject httpObject) {

								if (httpObject instanceof HttpRequest) {

									HttpRequest fullreq = (HttpRequest) httpObject;
									LOGGER.debug(
											"There is {} element in the content map ",
											LabriDefaultHttpProxyServer.this.content
													.size());
									if (fullreq.getUri().contains(".fr")) {
										if (LabriDefaultHttpProxyServer.this.content
												.containsKey(fullreq.getUri())) {

											fullreq.setUri(LabriDefaultHttpProxyServer.this.content
													.get(fullreq.getUri())
													.getNew_uri());
										} else {
											cacheService.askForCache(fullreq
													.getUri());
										}
									}
								}

								return null;
							}
						};
					}

					@Override
					public HttpFilters filterRequest(
							HttpRequest originalRequest,
							ChannelHandlerContext ctx) {
						return filterRequest(originalRequest);
					}

					@Override
					public int getMaximumRequestBufferSizeInBytes() {
						return Integer.MAX_VALUE;
					}

					@Override
					public int getMaximumResponseBufferSizeInBytes() {
						return Integer.MAX_VALUE;
					}
				})

				.start();

	}
}
