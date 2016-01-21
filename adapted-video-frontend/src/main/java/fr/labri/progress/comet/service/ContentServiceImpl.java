package fr.labri.progress.comet.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.Collection;
import javax.inject.Inject;
import org.glassfish.grizzly.utils.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import fr.labri.progess.comet.model.Content;
import fr.labri.progress.comet.conf.CliConfSingleton;
import fr.labri.progress.comet.exception.UnCachableContentException;
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
	public void addCacheRequest(Content content)
			throws UnCachableContentException {

		// if (!repo.findByOldUri(content.getUri()).isEmpty()) {

		// return;
		// }
		URI contentUri;
		try {
			contentUri = new URI(content.getUri()).normalize();
			if (contentUri.toString()
					.contains(CliConfSingleton.streamerBaseURL)) {
				LOGGER.debug("Streamer delivered video are not cached");
				throw new UnCachableContentException();
			}
		} catch (URISyntaxException e) {
			LOGGER.warn("invalid URI is ignored by frontal {}",
					content.getUri(), e);
			return;
		}
	
		CachedContent cachedContent = CachedContent.fromContent(content);
		if (cachedContent.getId() == null) {
			HashCode hash = Hashing.sha1().hashString(
					contentUri.toASCIIString(), Charsets.ASCII_CHARSET);
			cachedContent.setId(hash.toString());
		}

		if (repo.findOne(cachedContent.getId()) != null) {
			LOGGER.info(
					"already have a video transcoding for this URI {}  id: {}",
					cachedContent.getOldUri(), cachedContent.getId());
		} else {
			cachedContent.setRequestDate( new Date(System.currentTimeMillis()));
			repo.save(cachedContent);

			workerMessageService.sendTranscodeOrder(cachedContent.getOldUri()
					.toString(), cachedContent.getId());
			content.setId(cachedContent.getId());
		}

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

	@Override
	public Content getContent(String id) {
		return CachedContent.toContent(repo.findOne(id));
	
	}

}
