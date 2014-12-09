package fr.labri.progress.comet.repository;

import java.net.URI;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.labri.progress.comet.model.CachedContent;

public interface CachedContentRepository extends
		JpaRepository<CachedContent, String> {
	
	public List<CachedContent> findByOldUri(String uri);

}
