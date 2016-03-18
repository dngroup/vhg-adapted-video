package fr.labri.progress.comet.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import fr.labri.progress.comet.conf.CliConfSingleton;
import fr.labri.progress.comet.conf.SpringConfiguration;
import fr.labri.progress.comet.model.jackson.Qualities;
import fr.labri.progress.comet.model.jackson.Quality;

@Service
public class TranscodageProperties {
	private static final Logger LOGGER = LoggerFactory.getLogger(TranscodageProperties.class);
	public Integer H264_SOFT_HEIGHT = 720;
	public Integer H265_SOFT_HEIGHT = 720;
	public Integer H264_HARD_HEIGHT = 720;
	public Integer H265_HARD_HEIGHT = 720;
	public Integer H264_SOFT_BITRATE = 2000;
	public Integer H265_SOFT_BITRATE = 2000;
	public Integer H264_HARD_BITRATE = 2000;
	public Integer H265_HARD_BITRATE = 2000;

	public Qualities getTranscodageProperties() {
		ObjectMapper mapper = new ObjectMapper();
		InputStream input = null;
		// {"quality":[{"name":"HIGH_H264","bitrate":2000,"codec":"H264","height":720},{"name":"LOW_H264","bitrate":500,"codec":"H264","height":360},{"name":"LOW_H265","bitrate":500,"codec":"H265","height":360}]}

		if (!Strings.isNullOrEmpty(CliConfSingleton.TranscodageFile)) {
			try {

				input = new FileInputStream(CliConfSingleton.TranscodageFile);
				File file = new File(CliConfSingleton.TranscodageFile);
				Qualities qualities = mapper.readValue(file, Qualities.class);

				// load a properties file
				return qualities;
			} catch (IOException ex) {
				LOGGER.info("TRANSCOD_PARAM_FILE={} error with this file", CliConfSingleton.TranscodageFile);
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			LOGGER.info("no envirenement TRANSCOD_PARAM_FILE (need a link to a file). use default value");
			// String defaultQualities =
			// "{\"quality\":[{\"name\":\"HIGH_H264\",\"bitrate\":2000,\"codec\":\"H264\",\"height\":720},{\"name\":\"LOW_H264\",\"bitrate\":500,\"codec\":\"H264\",\"height\":360},{\"name\":\"LOW_H265\",\"bitrate\":500,\"codec\":\"H265\",\"height\":360}]}";

		}

		Qualities qualities = new Qualities();

		Quality quality = new Quality();
		quality.setBitrate(500);
		quality.setCodec("H264");
		quality.setName("360_default");
		quality.setHeight(360);
		qualities.addQuality(quality);

		Quality quality2 = new Quality();
		quality2.setBitrate(500);
		quality2.setCodec("H264");
		quality2.setName("test2");
		quality2.setHeight(360);
		qualities.addQuality(quality2);

		ObjectMapper mapper2 = new ObjectMapper();

		// Object to JSON in String
		try {
			LOGGER.debug(mapper2.writeValueAsString(qualities));

		} catch (JsonProcessingException e) {
			LOGGER.error("Can not convert you Transcode object to json", e);
			throw Throwables.propagate(e);
		}

		return qualities;
	}

}
