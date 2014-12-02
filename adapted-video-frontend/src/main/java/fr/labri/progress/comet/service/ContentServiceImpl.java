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

import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fr.labri.progess.comet.model.Content;
import fr.labri.progress.comet.exception.NoNewUriException;
import fr.labri.progress.comet.model.CachedContent;
import fr.labri.progress.comet.repository.CachedContentRepository;

@Service
public class ContentServiceImpl implements ContentService {

	@Inject
	CachedContentRepository repo;

	@Override
	public void addCacheRequest(Content content) {

		CachedContent cachedContent = CachedContent.fromContent(content);
		cachedContent.setId(UUID.randomUUID().toString());
		try {
			cachedContent.setNewUri(new URI("http://www.google.Fr"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repo.save(cachedContent);

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
	public String getUriFromId(String id) throws NoNewUriException {
		CachedContent cc = repo.findOne(id);
		if (cc != null) {
			return cc.getNewUri().toString();
		}

		throw new NoNewUriException();
	}

}
