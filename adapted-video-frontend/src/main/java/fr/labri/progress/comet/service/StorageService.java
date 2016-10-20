package fr.labri.progress.comet.service;

import java.net.URL;

public interface StorageService {

	/**
	 * create a place in the storage engine, to eventually push content to
	 * 
	 * @param id
	 *            the Id of the abstract resource enclosure
	 */
	void createStorageFolder(String id);

	/**
	 * 
	 * @param name
	 *            of the resource
	 * @param id
	 *            the Id of the abstract resource enclosure
	 * @return an URL that can be used to write a file remotely
	 */

	URL generateWriteURL(String name, String id);

	/**
	 * 
	 * @param name
	 *            of the resource
	 * @param id
	 *            the Id of the abstract resource enclosure
	 * @return an URL that can be used to write a file remotely
	 */
	URL generateReadURL(String name, String id);

}
