package fr.labri.progress.comet.service;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import javax.xml.bind.JAXBException;

import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.PlatformTransactionManager;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import fr.labri.progress.comet.conf.CliConfSingleton;
import fr.labri.progress.comet.model.CachedContent;
import fr.labri.progress.comet.repository.CachedContentRepository;

@Configuration
@EnableJpaRepositories("fr.labri.progress.comet.repository")
@ComponentScan(basePackages = { "fr.labri.progress.comet.repository",
		"fr.labri.progress.comet.service.StateMonitoringImp*" })
class MonitoringTestConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringTestConfig.class);

	protected final String celeryQueueName = "celery";

	@Bean
	public ObjectMapper getObjectMapper() {
		ObjectMapper om = new ObjectMapper();
		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		om.setAnnotationIntrospector(introspector);
		return om;

	}

	@Bean
	public DataSource ds() {

		return new JDBCDataSource();

	}

	@Bean(name = "transactionManager")
	@Inject
	public PlatformTransactionManager tm(EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}

	@Bean(name = "entityManagerFactory")
	@Inject
	public EntityManagerFactory emf() {

		return Persistence.createEntityManagerFactory("cache-orchestrator");
	}

	@Bean
	@Inject
	public RabbitAdmin getAdmin(ConnectionFactory cf) {
		RabbitAdmin admin = new RabbitAdmin(cf);
		return admin;
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		connectionFactory.setPort(5672);

		return connectionFactory;
	}

	@Bean
	@Inject
	public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
		RabbitTemplate template = new RabbitTemplate(cf);
		template.setRoutingKey(this.celeryQueueName);
		template.setEncoding("utf-8");

		return template;
	}

}

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { MonitoringTestConfig.class })
public class MonitoringTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringTest.class);
	@Inject
	CachedContentRepository cachedContentRepository;

	@Test
	public void getFromDB() {

		Date from = new Date(new Date().getTime() - 1000 * 100);
		CachedContent entity = new CachedContent();
		entity.setCreatedAt(from);
		entity.setDuration(1);
		entity.setId("test");
		cachedContentRepository.save(entity);

		Date from2 = new Date();
		CachedContent entity2 = new CachedContent();
		entity2.setCreatedAt(from2);
		entity2.setId("test2");
		entity2.setDuration(600);
		cachedContentRepository.save(entity2);

		Date from5 = new Date();
		CachedContent entity5 = new CachedContent();
		entity5.setCreatedAt(from5);
		entity5.setId("test5");
		cachedContentRepository.save(entity5);

		Date from3 = new Date();
		CachedContent entity3 = new CachedContent();
		entity3.setCreatedAt(from3);
		entity3.setId("test3");
		entity3.setDuration(300);
		cachedContentRepository.save(entity3);

		CachedContent entity4 = new CachedContent();
		entity4.setId("test4");
		entity4.setDuration(1);
		cachedContentRepository.save(entity4);

		// Date to = new Date(from2.getTime()+100000);

		StateMonitoringImp totest = new StateMonitoringImp();
		totest.repo = cachedContentRepository;
		int number = totest.getNbVideoLast(10);
		Assert.assertEquals(number, 3);

		int Second = totest.getSecondVideoLast(10);
		Assert.assertEquals(Second, 1500);

	}

	@Inject
	RabbitAdmin admin;
	@Test
	public void getFromRabbit() {

		StateMonitoringImp totest = new StateMonitoringImp();
		totest.admin = admin;
		int number = totest.getQueueSize();
		// Assert.assertEquals(number,3);
		//
		//
		// int Second = totest.getSecondVideoLast(10);
		// Assert.assertEquals(Second,1500);

	}

}
