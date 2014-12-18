package fr.labri.progress.comet.endpoint;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import jersey.repackaged.com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fr.labri.progess.comet.model.Content;
import fr.labri.progess.comet.model.ContentWrapper;
import fr.labri.progress.comet.conf.CliConfSingleton;
import fr.labri.progress.comet.exception.NoNewUriException;
import fr.labri.progress.comet.service.ContentService;

/**
 * provides access to content through jax-rs rest api
 * 
 * @author nherbaut
 *
 */
@Path("content")
public class ContentEndpoint {

	public ContentEndpoint() {
		LOGGER.trace("created");

	}

	private final static Logger LOGGER = LoggerFactory
			.getLogger(ContentEndpoint.class);

	@Autowired
	protected ContentService contentService = null;

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public ContentWrapper list() {
		ContentWrapper wrapper = new ContentWrapper();
		wrapper.setContents(Lists.newArrayList(contentService.getCache()));
		return wrapper;
	}

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response list(Content content) {
		LOGGER.info("we need to cache {}", content.getUri());
		contentService.addCacheRequest(content);
		LOGGER.info("now it's known under Id {}", content.getId());
		return Response.ok().build();
	}

	@Path("{contentId}/{quality}")
	@GET
	public Response getone(@PathParam("contentId") String contentId,@PathParam("quality") String quality)
			throws URISyntaxException {

		URI newUri = UriBuilder
				.fromPath(
						"http://" + CliConfSingleton.streamerHost + ":"
								+ CliConfSingleton.streamerPort)
				.path(contentId).path("encoding").path(quality+".mp4").build();
		return Response.seeOther(newUri).build();

	}
}
