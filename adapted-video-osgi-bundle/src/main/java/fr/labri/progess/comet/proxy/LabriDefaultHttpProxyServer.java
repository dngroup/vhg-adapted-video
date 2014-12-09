package fr.labri.progess.comet.proxy;

import fr.labri.progess.comet.config.LabriConfig;
import fr.labri.progess.comet.model.Content;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

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

	private final class LabriHttpFilterSource implements HttpFiltersSource {
		public HttpFilters filterRequest(HttpRequest originalRequest) {
			return new LabriHttpFiltersAdapter(originalRequest, null);
		}

		@Override
		public HttpFilters filterRequest(HttpRequest originalRequest,
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
	}

	private final class LabriHttpFiltersAdapter extends HttpFiltersAdapter {
		private LabriHttpFiltersAdapter(HttpRequest originalRequest,
				ChannelHandlerContext ctx) {
			super(originalRequest, ctx);
		}

		@Override
		public HttpObject serverToProxyResponse(HttpObject httpObject) {

			if (httpObject instanceof FullHttpMessage) {

				FullHttpMessage fullreq = (FullHttpMessage) httpObject;
				String headerValue = fullreq.headers().get("Content-Type");
				if (headerValue != null && headerValue.contains("video/mp4")) {
					cacheService.askForCache(this.originalRequest.getUri());
				}

			}

			return httpObject;

		}

		@Override
		public HttpResponse clientToProxyRequest(HttpObject httpObject) {
			if (httpObject instanceof FullHttpMessage) {

				FullHttpRequest fullreq = (FullHttpRequest) httpObject;
				if (content.containsKey(fullreq.getUri())
						&& !fullreq.headers()
								.contains("X-LABRI-TRAVERSE-PROXY")) {
					HttpResponse response = new DefaultHttpResponse(
							HttpVersion.HTTP_1_1,
							HttpResponseStatus.TEMPORARY_REDIRECT);
					response.headers().add("X-LABRI-TRAVERSE-PROXY", "");
					response.headers().add(
							"Location",
							"http://" + config.getFrontalHostName() + ":"
									+ config.getFrontalPort() + "/api/content/"
									+ content.get(fullreq.getUri()).getId());
					return response;
				}

			}
			return null;
		}
	}

	private static final Logger LOGGER = LoggerFactory
			.getLogger(LabriDefaultHttpProxyServer.class);
	HttpProxyServer server;
	protected ConcurrentMap<String, Content> content;
	final private CacheService cacheService = new CacheService();
	final private LabriConfig config;

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

	public LabriDefaultHttpProxyServer(LabriConfig config,
			final ConcurrentMap<String, Content> content) {
		this.content = content;
		this.config = config;
		LOGGER.debug("content is {}", this.content);
		InetSocketAddress addr = new InetSocketAddress(
				this.config.getHostName(), this.config.getPort());
		server = DefaultHttpProxyServer.bootstrap().withAddress(addr)
				.withFiltersSource(new LabriHttpFilterSource())

				.start();

	}
}
