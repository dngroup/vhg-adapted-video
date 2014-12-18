package fr.labri.progress.comet.conf;

import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyRequestEventListener implements RequestEventListener {
	private final int requestNumber;
	private final long startTime;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MyRequestEventListener.class);

	public MyRequestEventListener(int requestNumber) {
		this.requestNumber = requestNumber;
		startTime = System.currentTimeMillis();
	}

	@Override
	public void onEvent(RequestEvent event) {

		switch (event.getType()) {
		case RESOURCE_METHOD_START:
			LOGGER.trace("Resource method {} started for request ", event
					.getUriInfo().getMatchedResourceMethod().getHttpMethod(),
					requestNumber);

			break;
		case FINISHED:
			LOGGER.trace("Request {} finished. Processing time {} ms ",
					+requestNumber, (System.currentTimeMillis() - startTime));
			break;

		}
	}
}