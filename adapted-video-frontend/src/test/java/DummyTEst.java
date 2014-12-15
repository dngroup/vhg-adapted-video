import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

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
}
