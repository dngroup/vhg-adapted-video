package fr.labri.progress.comet.service;

import java.net.URL;

public interface SwiftService {

	void InitSecretKeys();

	URL GenerateReturnURI(String name, String id);

}
