package fr.labri.progess.comet.proxy;

import fr.labri.progess.comet.config.LabriConfig;
import fr.labri.progess.comet.model.FileType;
import fr.labri.progess.comet.model.FilterConfig;
import fr.labri.progess.comet.model.Content;
import fr.labri.progess.comet.model.HeaderFilter;
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

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.core.UriBuilder;

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
			return 0;
		}

		@Override
		public int getMaximumResponseBufferSizeInBytes() {
			return 0;
		}
	}

	private final class LabriHttpFiltersAdapter extends HttpFiltersAdapter {
		private LabriHttpFiltersAdapter(HttpRequest originalRequest,
				ChannelHandlerContext ctx) {
			super(originalRequest, ctx);
		}

		public HttpObject serverToProxyResponse(HttpObject httpObject) {

			if (httpObject instanceof DefaultHttpResponse) {

				DefaultHttpResponse fullreq = (DefaultHttpResponse) httpObject;

				HttpHeaders headers = fullreq.headers();
				final String uri = this.originalRequest.getUri();
				if (fullreq.getStatus().code() >= 200
						&& fullreq.getStatus().code() < 300) {
					if (checkForFilters(headers)
							|| checkForFileExtension(this.originalRequest
									.getUri())) {
						LOGGER.debug("asked for cache for resource {}", uri);
						cacheService.askForCache(this.originalRequest.getUri());
					} else {
						LOGGER.trace("resouce {} is NOT going to be cached",
								uri);
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

		public HttpResponse clientToProxyRequest(HttpObject httpObject) {
			if (httpObject instanceof DefaultHttpRequest) {

				DefaultHttpRequest fullreq = (DefaultHttpRequest) httpObject;
				Content c = null;
				// only return if content exist & at least one quality is
				// available
				if ((c = content.get(fullreq.getUri())) != null
						&& c.getQualities().size() > 0) {

					LOGGER.debug("Cached resource found {}", c.getUri());

					HttpResponse response = new DefaultHttpResponse(
							HttpVersion.HTTP_1_1,
							HttpResponseStatus.TEMPORARY_REDIRECT);
					Collections.shuffle(c.getQualities());

					String redirectUri = UriBuilder
							.fromPath(
									"http://" + config.getFrontalHostName()
											+ ":" + config.getFrontalPort())

							.path("api").path("content").path(c.getId())
							.path(c.getQualities().get(0)).build().toString();
					response.headers().add("Location", redirectUri);
					LOGGER.debug("Redirecting it to ", redirectUri);
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

	@Override
	public void abort() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setThrottle(long arg0, long arg1) {

	}
}
