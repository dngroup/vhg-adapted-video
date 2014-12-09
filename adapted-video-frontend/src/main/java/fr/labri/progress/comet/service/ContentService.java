package fr.labri.progress.comet.service;

import java.util.Collection;

import fr.labri.progess.comet.model.Content;
import fr.labri.progress.comet.exception.NoNewUriException;

public interface ContentService {

	public void addCacheRequest(Content content);

	public Collection<Content> getCache();

}
