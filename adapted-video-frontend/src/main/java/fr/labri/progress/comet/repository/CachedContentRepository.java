package fr.labri.progress.comet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.labri.progress.comet.model.CachedContent;

public interface CachedContentRepository extends
		JpaRepository<CachedContent, String> {

}
