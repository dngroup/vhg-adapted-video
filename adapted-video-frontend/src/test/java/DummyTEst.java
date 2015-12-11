import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import fr.labri.progress.comet.model.jackson.Kwargs;
import fr.labri.progress.comet.model.jackson.Qualities;
import fr.labri.progress.comet.model.jackson.Quality;
import fr.labri.progress.comet.model.jackson.Transcode;
import fr.labri.progress.comet.service.WorkerMessage;
import fr.labri.progress.comet.service.WorkerMessageServiceImpl;

public class DummyTEst {

	@Test
	public void testAll() throws JAXBException, JsonParseException, JsonMappingException, IOException {

		Unmarshaller unmashalled = JAXBContext.newInstance(WorkerMessage.class)
				.createUnmarshaller();

		ObjectMapper mapper = new ObjectMapper();
		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(
				TypeFactory.defaultInstance());
		mapper.setAnnotationIntrospector(introspector);
		WorkerMessage wm = mapper
				.readValue(
						"{\"quality\": \"medium\", \"main_task_id\": \"e563963a-7e2d-4c44-80af-b60682cd88f5\"}",
						WorkerMessage.class);
		System.out.println(wm);

	}
	
	@Test
	public void testMQM()   {

		Transcode transcode = new Transcode();
		transcode.setId("2222");
		transcode.setEta(ISODateTimeFormat.dateTimeNoMillis().print(
						DateTime.now().minusHours(200)));
		transcode.setRetries(1);
		transcode.setTask("adaptation.commons.encode_workflow");
		Kwargs kwargs = new Kwargs();
		kwargs.setUrl("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4?13");
		Qualities qualities = new Qualities();
		Quality quality = new Quality();
		quality.setBitrate(500);
		quality.setCodec("libx264");
		quality.setHeight(320);
		quality.setName("lowx264");
		qualities.addQuality(quality );
		Quality qualityx265 = new Quality();
		qualityx265.setBitrate(250);
		qualityx265.setCodec("libx265");
		qualityx265.setHeight(320);
		qualityx265.setName("lowx265");
		qualities.addQuality(qualityx265 );
		kwargs.setQualities(qualities );
		transcode.setKwargs(kwargs );
	
		System.out.println(transcode);
		
		
		
		String json = transcode.toJSON();
		System.out.println(json);
	}
	
	
	@Test
	public void testMQMold()  {
	

		String id ="1111";
		String uri= "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4?13";
		String message = "{\"id\": \""
				+ id 
				+ "\", \"task\": \"adaptation.commons.encode_workflow\", \"args\": [\""
				+ uri
				+ "\"], \"kwargs\": {}, \"retries\": 0, \"eta\": \""
				+ ISODateTimeFormat.dateTimeNoMillis().print(
						DateTime.now().minusHours(200)) + "\"}";
		
		
		
		
		System.out.println(message);
	}
}
