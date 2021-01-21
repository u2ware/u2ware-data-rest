package io.github.u2ware.data.test.ext01;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import io.github.u2ware.data.test.RestMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationConverterTests {

	protected Log logger = LogFactory.getLog(getClass());
	
	protected @Autowired MockMvc mockMvc;
	
	@Test
	public void contextLoads() throws Exception {

		RestMockMvc $ = new RestMockMvc(mockMvc, "");
		
		
		$.POST("/foos")
			.C("type", "AA")
			.C("date", "2010-01-01")
			.C("tags", new String[] {"a", "b", "c"})
			.is2xx()
		.andReturn("foo1");
		
		
		$.GET("{foo1}").is2xx();
		
	}
}
