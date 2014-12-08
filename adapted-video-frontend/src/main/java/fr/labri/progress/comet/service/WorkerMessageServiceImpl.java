package fr.labri.progress.comet.service;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

		Message amqpMessage = new Message(message.getBytes(), props);
		template.send(amqpMessage);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// nope

		}
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(conFact);
		container.setQueueNames(id.replace("-", ""));
		container.setMessageListener(new ChannelAwareMessageListener() {

			@Override
			public void onMessage(Message message, Channel channel)
					throws Exception {
				try {
					String messStr = message.toString();
					messStr = messStr.substring(7,
							messStr.indexOf("MessageProperties") - 1);
					ObjectMapper mapper = new ObjectMapper();
					EncodingTaskMessage resp = mapper.readValue(messStr,
							EncodingTaskMessage.class);
					resp.toString();
				} catch (JsonMappingException json) {
					System.out.println(json.getMessage());
				}

			}
		});

		container.start();

	}
}

// {
// "children": [],
// "result": {
// "hls":
// "/home/nicolas/output/bd1359c57a0541df92121fa9db2f5567/hls/300x200/playlist.m3u8 ²"
// },
// "status": "PARTIAL",
// "task_id": "7d6d385f-f170-44f5-ab57-1485dea1134c",
// "traceback": null
// }
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
class EncodingTaskMessage {
	public String getTraceback() {
		return traceback;
	}

	public void setTraceback(String traceback) {
		this.traceback = traceback;
	}

	Set<String> children;
	Map<String, String> result;
	String status;
	String task_id;
	String traceback;

	public Set<String> getChildren() {
		return children;
	}

	public void setChildren(Set<String> children) {
		this.children = children;
	}

	public Map<String, String> getResult() {
		return result;
	}

	public void setResult(Map<String, String> results) {
		this.result = results;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

}
