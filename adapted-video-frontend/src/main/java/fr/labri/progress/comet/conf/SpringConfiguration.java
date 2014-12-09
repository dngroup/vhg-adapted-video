package fr.labri.progress.comet.conf;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import fr.labri.progress.comet.service.WorkerMessageService;

/**
 * this class is responsible for configuring spring context and repositories
 * 
 * @author nherbaut
 *
 */
@Configuration
@ComponentScan(basePackages = { "fr.labri.progress.comet.service",
		"fr.labri.progress.comet.repository", "fr.labri.progress.comet.conf" })
@EnableJpaRepositories("fr.labri.progress.comet.repository")
@Import(RabbitMqConfiguration.class)
public class SpringConfiguration {

	
	@Inject
	WorkerMessageService wms;
	@PostConstruct
	public void setupQueue() {
		wms.setupResultQueue();
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

}
