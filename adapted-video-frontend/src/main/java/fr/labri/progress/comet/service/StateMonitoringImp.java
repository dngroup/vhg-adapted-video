/**
 * 
 */
package fr.labri.progress.comet.service;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

import fr.labri.progress.comet.model.CachedContent;
import fr.labri.progress.comet.repository.CachedContentRepository;

/**
 * @author dbourasseau
 *
 */
@Service
public class StateMonitoringImp implements StateMonitoring {
	private static final Logger LOGGER = LoggerFactory.getLogger(StateMonitoringImp.class);
	private static final int DEFAULT_DURATION = 600;

	@Inject
	volatile CachedContentRepository repo;

	@Inject
	volatile RabbitAdmin admin;

	@Inject
	volatile ConnectionFactory factory;

	public List<CachedContent> getVideoLast(long second) {
		Date to = new java.util.Date();
		Date from = new java.util.Date(to.getTime() - second * 1000);
		List<CachedContent> cachedContents = repo.findByCreatedAtBetween(from, to);

		return cachedContents;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.labri.progress.comet.service.StateMonitoring#getNbVideoLast(java.lang.
	 * String)
	 */
	@Override
	public int getNbVideoLast(long second) {
		return getVideoLast(second).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.labri.progress.comet.service.StateMonitoring#getSecondVideoLast(java.
	 * lang.String)
	 */
	@Override
	public int getSecondVideoLast(long second) {
		int duration = 0;
		List<CachedContent> cachedContents = getVideoLast(second);
		for (CachedContent cachedContent : cachedContents) {

			int contentDuration = cachedContent.getDuration();
			if (contentDuration == 0) {
				duration += DEFAULT_DURATION;
			} else {
				duration += contentDuration;
			}
		}
		return duration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.labri.progress.comet.service.StateMonitoring#getQueueSize(java.lang.
	 * String)
	 */
	@Override
	public int getQueueSize() {

		Properties props;
		Integer messageCount;
		Integer messageCountTotal = 0;
		int consumerCount;
		int counter = 0;
		String[] rabbitQueues = { "soft", "hard", "celery" };
		for (String queue : rabbitQueues) {
			props = admin.getQueueProperties(queue);
			try {
				messageCount = Integer.parseInt(props.get("QUEUE_MESSAGE_COUNT").toString());
				messageCountTotal += messageCount;
				consumerCount = Integer.parseInt(props.get("QUEUE_CONSUMER_COUNT").toString());

				LOGGER.info(queue + " has " + messageCount + " messages and " + consumerCount + " consumer");
			} catch (NullPointerException e) {
				counter++;
				LOGGER.info(queue + " is offline");
			}

		}
		if (counter == rabbitQueues.length) {
			return -1;
		}

		return messageCountTotal;
	}

}
