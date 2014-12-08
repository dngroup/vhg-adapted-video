package fr.labri.progress.comet.service;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJsonMessage {

	@Test
	public void testAll() throws JsonParseException, JsonMappingException, IOException {
		String messStr = "{\"status\": \"PARTIAL\", \"traceback\": null, \"result\": {\"hls\": \"/home/nicolas/output/bd1359c57a0541df92121fa9db2f5567/hls/300x200/playlist.m3u8\"}, \"task_id\": \"7d6d385f-f170-44f5-ab57-1485dea1134c\", \"children\": []}";
		ObjectMapper mapper = new ObjectMapper();
		EncodingTaskMessage resp = mapper.readValue(messStr,
				EncodingTaskMessage.class);
		resp.toString();
	}

}
