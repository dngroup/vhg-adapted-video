package fr.labri.progress.comet.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import fr.labri.progess.comet.model.Content;
import fr.labri.progress.comet.conf.CliConfSingleton;
import fr.labri.progress.comet.exception.NoNewUriException;
import fr.labri.progress.comet.model.CachedContent;
import fr.labri.progress.comet.repository.CachedContentRepository;

@Service
public class ContentServiceImpl implements ContentService {

	@Inject
	CachedContentRepository repo;

	@Inject
	WorkerMessageService workerMessageService;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ContentServiceImpl.class);

	@Override
	public void addCacheRequest(Content content) {

		if (!repo.findByOldUri(content.getUri()).isEmpty()) {
			LOGGER.info("already have a video transcoding for this URI, but it's not ready");
			return;
		}
		URI contentUri;
		try {
			contentUri = new URI(content.getUri());
			if (contentUri.getHost().equals(CliConfSingleton.streamerHost)) {
				LOGGER.debug("Streamer delivered video are not cached");
				return;
			}
		} catch (URISyntaxException e) {
			LOGGER.warn("invalid URI is ignored by frontal {}",
					content.getUri(), e);
		}

		CachedContent cachedContent = CachedContent.fromContent(content);
		cachedContent.setId(UUID.randomUUID().toString().replace("-", ""));
		repo.save(cachedContent);
		workerMessageService.sendDownloadOrder(cachedContent.getOldUri()
				.toString(), cachedContent.getId());
		content.setId(cachedContent.getId());

	}

	@Override
	public Collection<Content> getCache() {

		return Lists.transform(repo.findAll(),
				new Function<CachedContent, Content>() {

					@Override
					public Content apply(CachedContent input) {
						return CachedContent.toContent(input);
					}
				});

	}

}
