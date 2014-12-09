package fr.labri.progess.comet.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class LabriConfigImpl implements LabriConfig {

	Properties props = new Properties();
	private static final String CONFIG_FILE = "/var/local/labri/config.properties";

	public LabriConfigImpl() {

		try {
			props.load(new FileReader(CONFIG_FILE));
		} catch (IOException e) {
			throw new RuntimeException("failed to load properties in "
					+ CONFIG_FILE, e);
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

}
