package fr.labri.progress.comet.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import fr.labri.progress.comet.conf.CliConfSingleton;

public class TranscodageProperties {

	public  Integer H264_SOFT_HEIGHT=720;
	public  Integer H265_SOFT_HEIGHT=720;
	public  Integer H264_HARD_HEIGHT=720;
	public  Integer H265_HARD_HEIGHT=720;
	public  Integer H264_SOFT_BITRATE=2000;
	public  Integer H265_SOFT_BITRATE=2000;
	public  Integer H264_HARD_BITRATE=2000;
	public  Integer H265_HARD_BITRATE=2000;

	public TranscodageProperties() {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(CliConfSingleton.TranscodageFile);
			// load a properties file
			prop.load(input);
			H264_SOFT_HEIGHT = Integer.valueOf(prop.getProperty("H264_SOFT_HEIGHT"));
			H265_SOFT_HEIGHT = Integer.valueOf(prop.getProperty("H265_SOFT_HEIGHT"));
			H264_HARD_HEIGHT = Integer.valueOf(prop.getProperty("H264_HARD_HEIGHT"));
			H265_HARD_HEIGHT = Integer.valueOf(prop.getProperty("H265_HARD_HEIGHT"));
			H264_SOFT_BITRATE = Integer.valueOf(prop.getProperty("H264_SOFT_BITRATE"));
			H265_SOFT_BITRATE = Integer.valueOf(prop.getProperty("H265_SOFT_BITRATE"));
			H264_HARD_BITRATE = Integer.valueOf(prop.getProperty("H264_HARD_BITRATE"));
			H265_HARD_BITRATE = Integer.valueOf(prop.getProperty("H265_HARD_BITRATE"));

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
