package fr.labri.progess.comet.proxy;

import fr.labri.progess.comet.config.LabriConfig;
import fr.labri.progess.comet.model.FileType;
import fr.labri.progess.comet.model.FilterConfig;
import fr.labri.progess.comet.model.Content;
import fr.labri.progess.comet.model.HeaderFilter;
import io.netty.channel.ChannelHandlerContext;
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

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSource;
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

				FullHttpResponse fullreq = (FullHttpResponse) httpObject;

				HttpHeaders headers = fullreq.headers();
				if (fullreq.getStatus().code() >= 200
						&& fullreq.getStatus().code() < 300) {
					if (checkForFilters(headers)
							|| checkForFileExtension(this.originalRequest
									.getUri())) {
						LOGGER.debug("asked for cache for resource {}",
								this.originalRequest.getUri());
						cacheService.askForCache(this.originalRequest.getUri());
					}
				}

			}

			return httpObject;

		}

		private boolean checkForFileExtension(String uri) {
			for (FilterConfig filterConfig : filterConfigs) {
				for (FileType types : filterConfig.getFileTypes()) {
					if (uri.endsWith(types.getExtension())) {
						return true;
					}
				}
			}
			return false;
		}

		private boolean checkForFilters(HttpHeaders headers) {
			for (FilterConfig filterConfig : filterConfigs) {

				for (HeaderFilter headerFilter : filterConfig.getHeaderValues()) {

					String headerValue = headers.get(headerFilter.getHeader());
					if (headerValue != null
							&& headerValue.toLowerCase().contains(
									headerFilter.getValue().toLowerCase())
							&& !content.containsKey(this.originalRequest
									.getUri())) {
						return true;

					}
				}

			}
			return false;
		}

		@Override
		public HttpResponse clientToProxyRequest(HttpObject httpObject) {
			if (httpObject instanceof FullHttpMessage) {

				FullHttpRequest fullreq = (FullHttpRequest) httpObject;
				if (content.containsKey(fullreq.getUri())
						&& !fullreq.headers()
								.contains("X-LABRI-TRAVERSE-PROXY")) {

					LOGGER.debug("Potentially cached resource foudn");

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
	final protected ConcurrentMap<String, Content> content;
	final private CacheService cacheService;
	final private LabriConfig config;
	final private Set<FilterConfig> filterConfigs;

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
			final ConcurrentMap<String, Content> content,
			final Set<FilterConfig> filterConfigs) {
		this.content = content;
		this.config = config;
		this.filterConfigs = filterConfigs;
		this.cacheService = new CacheService(this.config.getFrontalHostName(),
				this.config.getFrontalPort());
		LOGGER.debug("content is {}", this.content);
		InetSocketAddress addr = new InetSocketAddress(
				this.config.getHostName(), this.config.getPort());
		server = DefaultHttpProxyServer.bootstrap().withAddress(addr)
				.withFiltersSource(new LabriHttpFilterSource())

				.start();

	}
}
