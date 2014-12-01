package fr.labri.progress.comet;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.labri.progess.comet.model.Content;
import fr.labri.progess.comet.model.ContentWrapper;
import fr.labri.progress.comet.exception.NoNewUriException;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("content")
public class ContentEndpoint {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(ContentEndpoint.class);

	private static final ContentService contentService = new ContentServiceImpl();

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

	@Path("{contentId}")
	@GET
	public Response getone(@PathParam("contentId") String contentId)
			throws URISyntaxException {
		try {
			return Response.seeOther(
					new URI(contentService.getUriFromId(contentId))).build();
		} catch (NoNewUriException e) {
			throw new WebApplicationException(404);
		}

	}
}
