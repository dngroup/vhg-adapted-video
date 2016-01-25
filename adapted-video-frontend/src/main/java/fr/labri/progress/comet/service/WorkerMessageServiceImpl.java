package fr.labri.progress.comet.service;

import java.net.URI;
import java.sql.Date;
import java.util.Collections;

import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

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
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;

import fr.labri.progress.comet.conf.CliConfSingleton;
import fr.labri.progress.comet.model.CachedContent;
import fr.labri.progress.comet.model.jackson.Kwargs;
import fr.labri.progress.comet.model.jackson.Qualities;
import fr.labri.progress.comet.model.jackson.Quality;
import fr.labri.progress.comet.model.jackson.Transcode;
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

	private static final String TASK = "adaptation.commons.staging_and_admission_workflow";

	private static final MessageProperties props = new MessageProperties();

	public enum Encoder { 
		H264("h264"), H265("h265"),H264SOFT("SOFTh264"), H265SOFT("SOFTh265"),H264HARD("HARDh264"), H265HARD("HARDh265");
		private String name = "";

		Encoder(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	{
		props.setContentType("application/json");
		props.setContentEncoding("utf-8");

	}

	@Override
	public void sendTranscodeOrder(final String uri, final String id) {

		LOGGER.info("download order for id {}", id);

		Transcode transcode = new Transcode();
		transcode.setId(id);
		transcode.setEta(ISODateTimeFormat.dateTimeNoMillis().print(
				DateTime.now().minusHours(200)));
		transcode.setRetries(1);
		URI urireturn = UriBuilder.fromUri("http://"+CliConfSingleton.frontendHostname).port(CliConfSingleton.frontendPort).path("api").path("content").path(id).build();
		transcode.setReturnAddr(urireturn.toString());
		Kwargs kwargs = new Kwargs();
		kwargs.setUrl(uri);

		Qualities qualities;

		transcode.setTask(TASK);
		qualities = getQuality();

		kwargs.setQualities(qualities);
		transcode.setKwargs(kwargs);

		String message = transcode.toJSON();

		LOGGER.debug(message);
		Message amqpMessage = new Message(message.getBytes(), props);
		LOGGER.debug(message);
		template.send(amqpMessage);

	}

	/**
	 * @return qualities
	 */
	private Qualities getQuality() {
		Qualities qualities = new Qualities();

		Quality h264_soft = new Quality();
		h264_soft.setBitrate(2000);
		h264_soft.setCodec(Encoder.H264SOFT.toString());
		h264_soft.setHeight(720);
		h264_soft.setName(Encoder.H264SOFT.toString());
		qualities.addQuality(h264_soft);

		Quality h265_soft = new Quality();
		h265_soft.setBitrate(2000);
		h265_soft.setCodec(Encoder.H265SOFT.toString());
		h265_soft.setHeight(720);
		h265_soft.setName(Encoder.H265SOFT.toString());
		qualities.addQuality(h265_soft);
		
		Quality h264_hard = new Quality();
		h264_hard.setBitrate(2000);
		h264_hard.setCodec(Encoder.H264HARD.toString());
		h264_hard.setHeight(720);
		h264_hard.setName(Encoder.H264HARD.toString());
		qualities.addQuality(h264_hard);

		Quality h265_hard = new Quality();
		h265_hard.setBitrate(2000);
		h265_hard.setCodec(Encoder.H265HARD.toString());
		h265_hard.setHeight(720);
		h265_hard.setName(Encoder.H265HARD.toString());
		qualities.addQuality(h265_hard);
		
		return qualities;
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
				LOGGER.warn(
						"error received while using rabbitmq container, trying to redeclare the queue",
						t);
				admin.declareQueue(new org.springframework.amqp.core.Queue(
						RESULTQUEUE, true, false, false, Collections.EMPTY_MAP));

			}
		});
		container.start();
	}


}
