package fr.labri.progress.comet.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import fr.labri.progress.comet.conf.CliConfSingleton;
import fr.labri.progress.comet.model.jackson.Quality;

@Service
public class QualityServiceImp implements QualityService {
	private static final Logger LOGGER = LoggerFactory.getLogger(QualityServiceImp.class);
	public Integer H264_SOFT_HEIGHT = 720;
	public Integer H265_SOFT_HEIGHT = 720;
	public Integer H264_SOFT_BITRATE = 1500;
	public Integer H265_SOFT_BITRATE = 1000;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.labri.progress.comet.service.QualityService#getTranscodageProperties()
	 */
	@Override
	public List<Quality> getTranscodageProperties() {
		ObjectMapper mapper = new ObjectMapper();
		InputStream input = null;
		if (!Strings.isNullOrEmpty(CliConfSingleton.TranscodageFile)) {
			try {

				input = new FileInputStream(CliConfSingleton.TranscodageFile);
				File file = new File(CliConfSingleton.TranscodageFile);
				List<Quality> qualities = mapper.readValue(file, new TypeReference<List<Quality>>() {
				});

				// load a properties file
				return qualities;
			} catch (IOException ex) {
				LOGGER.info("TRANSCOD_PARAM_FILE={} error with this file set default value",
						CliConfSingleton.TranscodageFile);
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						LOGGER.warn("input can not be close");
						;
					}
				}
			}
		} else {
			LOGGER.info("no envirenement TRANSCOD_PARAM_FILE (need a link to a file). use default value");
			// String defaultQualities =
			// "{\"quality\":[{\"name\":\"HIGH_H264\",\"bitrate\":2000,\"codec\":\"H264\",\"height\":720},{\"name\":\"LOW_H264\",\"bitrate\":500,\"codec\":\"H264\",\"height\":360},{\"name\":\"LOW_H265\",\"bitrate\":500,\"codec\":\"H265\",\"height\":360}]}";

		}

		// LOAD DEFAULT VALUE
		List<Quality> qualities = new ArrayList<Quality>();

		Quality quality = new Quality();
		quality.setBitrate(H264_SOFT_BITRATE);
		quality.setCodec("H264");
		quality.setName("360H264_default");
		quality.setHeight(H264_SOFT_HEIGHT);
		qualities.add(quality);

		Quality quality2 = new Quality();
		quality2.setBitrate(H265_SOFT_BITRATE);
		quality2.setCodec("H265");
		quality2.setName("360H265_default");
		quality2.setHeight(H265_SOFT_HEIGHT);
		qualities.add(quality2);

		return qualities;
	}

}
