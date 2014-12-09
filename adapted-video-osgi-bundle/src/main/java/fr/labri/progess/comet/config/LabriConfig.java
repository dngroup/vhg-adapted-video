package fr.labri.progess.comet.config;

import com.lexicalscope.jewel.cli.Option;

public interface LabriConfig {
	@Option(longName="host", defaultValue = "0.0.0.0" )
	String getHostName();

	@Option(longName="port",  defaultValue = "8080")
	Integer getPort();

	@Option(longName="frontalHostName",defaultValue = "frontal")
	String getFrontalHostName();

	@Option(longName="frontalPort", defaultValue = "8080")
	Integer getFrontalPort();
}
