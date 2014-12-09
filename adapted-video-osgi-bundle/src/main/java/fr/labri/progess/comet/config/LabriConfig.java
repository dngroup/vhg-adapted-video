package fr.labri.progess.comet.config;

import com.lexicalscope.jewel.cli.Option;

public interface LabriConfig {
	@Option(defaultValue = "0.0.0.0", helpRequest = true)
	String getHostName();

	@Option(defaultValue = "8084", helpRequest = true)
	Integer getPort();

	@Option(defaultValue = "localhost", helpRequest = true)
	String getFrontalHostName();

	@Option(defaultValue = "8082", helpRequest = true)
	Integer getFrontalPort();
}
