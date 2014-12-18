package fr.labri.progress.comet.conf;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyApplicationEventListener implements ApplicationEventListener {
	private volatile int requestCnt = 0;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MyApplicationEventListener.class);

	@Override
	public void onEvent(ApplicationEvent event) {
		switch (event.getType()) {
		case INITIALIZATION_FINISHED:
			LOGGER.trace("Application {} was initialized.", event
					.getResourceConfig().getApplicationName());

			break;
		case DESTROY_FINISHED:
			LOGGER.trace("Application {} destroyed", event.getResourceConfig()
					.getApplicationName());

			break;
		default:
			break;
		}
	}

	@Override
	public RequestEventListener onRequest(RequestEvent requestEvent) {
		requestCnt++;
		LOGGER.trace("Request {} started", requestEvent.getUriInfo()
				.getRequestUri());

		// return the listener instance that will handle this request.
		return new MyRequestEventListener(requestCnt);
	}
}