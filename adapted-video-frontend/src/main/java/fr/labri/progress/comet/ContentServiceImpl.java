package fr.labri.progress.comet;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import fr.labri.progess.comet.model.Content;
import fr.labri.progress.comet.exception.NoNewUriException;

public class ContentServiceImpl implements ContentService {

	private static final ConcurrentMap<String, Content> map = new ConcurrentHashMap<String, Content>();

	@Override
	public void addCacheRequest(Content content) {
		content.setId(UUID.randomUUID().toString());
		content.setNew_uri("http://google.fr");
		content.setCreated(new Date());
		map.put(content.getUri(), content);

	}

	@Override
	public Collection<Content> getCache() {

		return map.values();
	}

	@Override
	public String getUriFromId(String id) throws NoNewUriException {
		for (Content cont : map.values()) {
			if (id.equals(cont.getId())) {
				return cont.getNew_uri();
			}
		}
		throw new NoNewUriException();
	}

}
