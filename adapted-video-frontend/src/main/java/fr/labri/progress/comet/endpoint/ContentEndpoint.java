package fr.labri.progress.comet.endpoint;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fr.labri.progess.comet.model.Content;
import fr.labri.progess.comet.model.ContentWrapper;
import fr.labri.progress.comet.conf.CliConfSingleton;
import fr.labri.progress.comet.exception.UnCachableContentException;
import fr.labri.progress.comet.service.ContentService;
import jersey.repackaged.com.google.common.collect.Lists;

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
	
	@GET
	@Path("{contentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Content getStatusEntity(@PathParam("contentId") String contentId) {
		Content content = contentService.getContent(contentId);
		return content;
	}

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response postContent(Content content) {

		try {
			LOGGER.trace("new content {} candidate for caching",
					content.getUri() );
			contentService.addCacheRequest(content);
			
			return Response.ok().build();
		} catch (UnCachableContentException e) {
			return Response.noContent().build();
		}

	}

	@Path("{contentId}/{quality}")
	@GET
	public Response getone(@PathParam("contentId") String contentId,
			@PathParam("quality") String quality) throws URISyntaxException {

		URI newUri = UriBuilder.fromPath(CliConfSingleton.streamerBaseURL)
				.path(contentId).path(quality)
				.build();
		return Response.seeOther(newUri).build();

	}
	@Path("{contentId}/{quality}")
	@POST
	public Response postfile(@PathParam("contentId") String contentId,
			@PathParam("quality") String quality, InputStream is) throws URISyntaxException {

		URI newUri = UriBuilder.fromPath("http://"+ CliConfSingleton.storageHostname+":8079/api/storage")
				.path(contentId).path(quality + ".mp4")
				.build();
		LOGGER.debug("redirect to {}",newUri.toString());
		return Response.temporaryRedirect(newUri).build();

	}
	
	

	
}
