package fr.labri.progress.comet.service;

import java.util.List;

import fr.labri.progress.comet.model.jackson.Quality;

public interface QualityService {

	/**
	 * Get list of quality from file 
	 * @return a list of qualities
	 */
	List<Quality> getTranscodageProperties();

}