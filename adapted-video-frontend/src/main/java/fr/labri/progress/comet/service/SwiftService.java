package fr.labri.progress.comet.service;

import java.net.URL;

public interface SwiftService {

	

	URL GenerateReturnURI(String name, String id);


	void loginAndCreateContainer(String id);


	URL GenerateReturnURI(String name, String id, String methode);

}
