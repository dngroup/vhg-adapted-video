package fr.labri.progress.comet.service;

import java.util.Collection;

import fr.labri.progess.comet.model.Content;
import fr.labri.progress.comet.exception.NoNewUriException;
import fr.labri.progress.comet.exception.UnCachableContentException;

public interface ContentService {

	public void addCacheRequest(Content content) throws UnCachableContentException;

	public Collection<Content> getCache();
	
	public Content getContent(String id);

}
