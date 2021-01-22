package io.github.u2ware.data.test.example07;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.google.common.collect.Maps;

import io.github.u2ware.data.test.RestMockMvc;

	
@SpringBootTest
@AutoConfigureMockMvc
public class CompositeIdEntityTests {

	protected Log logger = LogFactory.getLog(getClass());

	protected @Autowired MockMvc mockMvc;
	
	
	
	@Test
	public void contextLoads() throws Exception{
		RestMockMvc $ = new RestMockMvc(mockMvc, "");
		
		
		$.GET("/profile").is2xx();
		
		Map<String,String> id = Maps.newHashMap();
		id.put("key1", "key1");
		id.put("key2", "key2");
		
		$.POST("/compositeIdEntities").C("id", id).C("stringValue", "stringValue").is2xx().andReturn("d1");
		
		$.GET("/compositeIdEntities").is2xx();
		
		$.GET("{d1}").is2xx();

	}

	
	
	
}
