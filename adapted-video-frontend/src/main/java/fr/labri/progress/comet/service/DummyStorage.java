package fr.labri.progress.comet.service;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


public class DummyStorage implements StorageService {
	private static Logger LOGGER = LoggerFactory.getLogger(DummyStorage.class);

	
	@Override
	public void createStorageFolder(String id) {

	}

	@Override
	public URL generateWriteURL(String name, String id) {
		URL dummy =null;
		try {
			dummy = new URL("http://dummy.com/id/name");
			return dummy;
		} catch (MalformedURLException e) {
			LOGGER.error("dummy url not correct");
		}
		return dummy;
		
	}

	@Override
	public URL generateReadURL(String name, String id) {
		URL dummy =null;
		try {
			dummy = new URL("http://dummy.com/id/name");
			return dummy;
		} catch (MalformedURLException e) {
			LOGGER.error("dummy url not correct");
		}
		return dummy;
		
	}

}
