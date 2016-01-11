package fr.labri.progress.comet.conf;

import java.util.Collections;

import javax.inject.Inject;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

	protected final String celeryQueueName = "celery";

	@Bean
	@Inject
	public RabbitAdmin getAdmin(ConnectionFactory cf) {
		RabbitAdmin admin = new RabbitAdmin(cf);
//		admin.declareQueue(new org.springframework.amqp.core.Queue("soft",
//				true, false, false, Collections.EMPTY_MAP));
//		admin.declareQueue(new org.springframework.amqp.core.Queue("hard",
//				true, false, false, Collections.EMPTY_MAP));
		
		return admin;
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
				CliConfSingleton.rabbitHost);
		connectionFactory.setUsername(CliConfSingleton.rabbitUser);
		connectionFactory.setPassword(CliConfSingleton.rabbitPassword);
		connectionFactory.setPort(CliConfSingleton.rabbitPort);

		return connectionFactory;
	}

	@Bean
	@Inject
	public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
		RabbitTemplate template = new RabbitTemplate(cf);
		
		template.setRoutingKey(celeryQueueName);
		template.setEncoding("utf-8");

		return template;
	}

}
