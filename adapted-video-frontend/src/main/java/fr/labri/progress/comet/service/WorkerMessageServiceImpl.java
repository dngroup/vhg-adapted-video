package fr.labri.progress.comet.service;

import java.sql.Date;
import java.util.Collections;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.Queue;
import com.rabbitmq.client.Channel;

import fr.labri.progress.comet.model.CachedContent;
import fr.labri.progress.comet.repository.CachedContentRepository;

@Service
public class WorkerMessageServiceImpl implements WorkerMessageService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(WorkerMessageServiceImpl.class);

	public static final String RESULTQUEUE = "transcode-result";
	@Inject
	volatile RabbitTemplate template;

	@Inject
	volatile RabbitAdmin admin;

	@Inject
	volatile ConnectionFactory conFact;

	@Inject
	volatile CachedContentRepository repo;

	@Inject
	ObjectMapper mapper;

	private static final MessageProperties props = new MessageProperties();

	{
		props.setContentType("application/json");
		props.setContentEncoding("utf-8");

	}

	@Override
	public void sendDownloadOrder(final String uri, final String id) {

		LOGGER.info("download order for id {}", id);
		String message = "{\"id\": \""
				+ id
				+ "\", \"task\": \"adaptation.commons.encode_workflow\", \"args\": [\""
				+ uri
				+ "\"], \"kwargs\": {}, \"retries\": 0, \"eta\": \""
				+ ISODateTimeFormat.dateTimeNoMillis().print(
						DateTime.now().minusHours(200)) + "\"}";

		Message amqpMessage = new Message(message.getBytes(), props);
		template.send(amqpMessage);

	}

	@Override
	public void setupResultQueue() {

		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(conFact);
		container.setQueueNames(RESULTQUEUE);

		container.setMessageListener(new ChannelAwareMessageListener() {

			SimpleMessageConverter conv = new SimpleMessageConverter();

			@Override
			public void onMessage(Message message, Channel channel)
					throws Exception {

				WorkerMessage wm = mapper.readValue(
						(byte[]) conv.fromMessage(message), WorkerMessage.class);
				CachedContent content = repo.findOne(wm.getMain_task_id());
				if (content != null) {
					if (wm.getComplete() == null || !wm.getComplete()) {
						LOGGER.debug(
								"new quality {} received for content id:{}",
								wm.getQuality(), wm.getMain_task_id());
						content.getQualities().add(wm.getQuality());
					} else {
						LOGGER.debug("work on content id:{} is done",
								wm.getMain_task_id());
						content.setCreatedAt(new Date(System
								.currentTimeMillis()));
					}
					
					repo.save(content);
				} else {
					LOGGER.warn(
							"received received job message for unknown task {}",
							wm.main_task_id);
				}

			}
		});

		// declaring the queue if it's not already present
		admin.declareQueue(new org.springframework.amqp.core.Queue(RESULTQUEUE,
				true, false, false, Collections.EMPTY_MAP));

		container.setErrorHandler(new ErrorHandler() {

			@Override
			public void handleError(Throwable t) {
				LOGGER.warn("error received while using rabbitmq container, trying to redeclare the queue");
				admin.declareQueue(new org.springframework.amqp.core.Queue(
						RESULTQUEUE, true, false, false, Collections.EMPTY_MAP));

			}
		});
		container.start();
	}
}
