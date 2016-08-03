package fr.labri.progress.comet.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.labri.progress.comet.model.CachedContent;

public interface CachedContentRepository extends
		JpaRepository<CachedContent, String> {
	
	public List<CachedContent> findByOldUri(String uri);
	
//	 @Query(value = "select p from CachedContent p left join fetch p.qualities")
//	 @EntityGraph(value = "CachedContent.detail", type = EntityGraphType.LOAD)
//	 List<CachedContent> getAllCachedContent();
	
	
	List<CachedContent>  findByCreatedAtBetween(Date from ,Date to);

}
