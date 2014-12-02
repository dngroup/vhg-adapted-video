package fr.labri.progress.comet.service;

import java.util.UUID;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class WorkerMessageServiceImpl implements WorkerMessageService {

	@Inject
	volatile RabbitTemplate template;

	private static final MessageProperties props = new MessageProperties();

	{
		props.setContentType("application/json");
		props.setContentEncoding("urf-8");

	}

	@Override
	public void sendDownloadOrder(String uri, String id) {

		String message = "{\"id\": \"" + UUID.randomUUID().toString()
				+ "\", \"task\": \"tasks.download\", \"args\": [\"" + uri + "\",\""
				+ id + "\"], \"kwargs\": {}, \"retries\": 0, \"eta\": \""
				+ ISODateTimeFormat.dateTimeNoMillis().print(DateTime.now()) + "\"}";

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

	}

}
