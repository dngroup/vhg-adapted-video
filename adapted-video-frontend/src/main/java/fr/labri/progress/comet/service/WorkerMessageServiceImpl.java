package fr.labri.progress.comet.service;

import java.util.UUID;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;

@Service
public class WorkerMessageServiceImpl implements WorkerMessageService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(WorkerMessageServiceImpl.class);
	@Inject
	volatile RabbitTemplate template;

	@Inject
	volatile ConnectionFactory conFact;

	private static final MessageProperties props = new MessageProperties();

	{
		props.setContentType("application/json");
		props.setContentEncoding("urf-8");

	}

	@Override
	public void sendDownloadOrder(String uri, String id) {

		LOGGER.info("download order for id {}", id);
		String message = "{\"id\": \""
				+ id
				+ "\", \"task\": \"adaptation.commons.encode_workflow\", \"args\": [\""
				+ uri
				+ "\"], \"kwargs\": {}, \"retries\": 0, \"eta\": \""
				+ ISODateTimeFormat.dateTimeNoMillis().print(
						DateTime.now().minusHours(3)) + "\"}";

		// String QUEUE_NAME = "celery";
		// ConnectionFactory factory = new ConnectionFactory();
		// factory.setHost("localhost");
		// Connection connection;
		//
		// connection = factory.newConnection();
		//
		// Channel channel = connection.createChannel();
		// channel.queueDeclare(QUEUE_NAME, true, false, false, null);
		//
		// channel.basicPublish(
		// "",
		// QUEUE_NAME,
		// new AMQP.BasicProperties.Builder()
		// .contentType("application/json")
		// .contentEncoding("utf-8").build(),
		// message.getBytes("utf-8"));
		// System.out.println(" [x] Sent '" + message + "'");
		// channel.close();
		// connection.close();

		Message amqpMessage = new Message(message.getBytes(), props);
		template.send(amqpMessage);

		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(conFact);
		container.setQueueNames(id.replace("-", ""));
		container.setMessageListener(new ChannelAwareMessageListener() {

			@Override
			public void onMessage(Message message, Channel channel)
					throws Exception {
				System.out.println(message.toString());

			}
		});

		container.start();

	}
}
