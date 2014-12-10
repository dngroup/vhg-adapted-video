package fr.labri.progress.comet.endpoint;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fr.labri.progess.comet.model.FileType;
import fr.labri.progess.comet.model.FilterConfig;
import fr.labri.progess.comet.model.FilterConfigWrapper;
import fr.labri.progess.comet.model.HeaderFilter;

@Path("config")
public class ConfigurationEndpoint {

	@Path("{deviceId}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public FilterConfigWrapper getConfig(@PathParam("deviceId") String deviceId) {
		// hardcoded dummy values TODO implement logic

		FilterConfigWrapper wrapper = new FilterConfigWrapper();
		FilterConfig config = new FilterConfig();
		FileType mp4 = new FileType();
		mp4.setExtension("aac");
		config.getFileTypes().add(mp4);

		HeaderFilter headerValue = new HeaderFilter();
		headerValue.setHeader("Content-Type");
		headerValue.setValue("video/mp4");
		config.getHeaderValues().add(headerValue);

		wrapper.getFilterConfigs().add(config);


		return wrapper;

	}
}
