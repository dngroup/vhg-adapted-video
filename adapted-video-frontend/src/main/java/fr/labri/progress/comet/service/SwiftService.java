package fr.labri.progress.comet.service;

import java.net.URL;

public interface SwiftService {

	




	/**
	 * Set shared key and create container
	 * @param id of container
	 */
	void loginAndCreateContainer(String id);

	/**
	 * Generate a URL for a other entity (worker) can push (PUT) content on swift
	 * @param Name of the quality
	 * @param id of container
	 * @return url to push content
	 */
	URL GenerateReturnURI(String name, String id);
	

	/**
	 * Generate a URL for a other entity (worker) can get push push content on swift
	 * @param Name of the quality
	 * @param id of container
	 * @param rest method method 
	 * @return url to push content
	 */
	URL GenerateReturnURI(String name, String id, String method);

}
