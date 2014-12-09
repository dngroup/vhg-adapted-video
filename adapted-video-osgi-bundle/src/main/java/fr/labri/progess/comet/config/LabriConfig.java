package fr.labri.progess.comet.config;

import com.lexicalscope.jewel.cli.Option;

public interface LabriConfig {
	@Option(defaultValue = "localhost", helpRequest = true)
	String getHostName();

	@Option(defaultValue = "8084", helpRequest = true)
	Integer getPort();
}
