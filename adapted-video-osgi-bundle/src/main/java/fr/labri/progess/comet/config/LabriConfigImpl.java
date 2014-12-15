package fr.labri.progess.comet.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LabriConfigImpl implements LabriConfig {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(LabriConfigImpl.class);
	Properties props = new Properties();
	private static final String CONFIG_FILE = "/var/local/labri/config.properties";

	public LabriConfigImpl() {

		try {
			props.load(new FileReader(CONFIG_FILE));
		} catch (IOException e) {
			LOGGER.warn(
					"failed to read config file {}, default values will be used",
					CONFIG_FILE);
		}
	}

	@Override
	public String getHostName() {
		return props.getProperty("host", "0.0.0.0");
	}

	@Override
	public Integer getPort() {
		return Integer.valueOf(props.getProperty("port", "8080"));
	}

	@Override
	public String getFrontalHostName() {
		return props.getProperty("frontalHost", "frontal");
	}

	@Override
	public Integer getFrontalPort() {
		return Integer.valueOf(props.getProperty("frontalPort", "8080"));
	}

	@Override
	public boolean getHelp() {
		throw new RuntimeException(
				"that's a dummy method and should not be called");
	}

}
