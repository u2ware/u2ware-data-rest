package io.github.u2ware.data.test.example05;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ClassUtils;

import io.github.u2ware.data.test.RestMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationProcessorTests {

	protected Log logger = LogFactory.getLog(getClass());

	protected @Autowired BaseEntityRepository baseEntityRepository;
	protected @Autowired ManyToOneEntityRepository manyToOneEntityRepository;
	protected @Autowired MockMvc mockMvc;

	@Test
	public void contextLoads() throws Exception{
		RestMockMvc $ = new RestMockMvc(mockMvc, "");

		///////////////////////////////////////////
		//
		///////////////////////////////////////////
		$.POST("/manyToOneEntities").C(ManyToOneEntity.builder().name("mto_a").age(1).build()).is2xx().andReturn("m1");

		$.GET("{m1}").is2xx()
		.andExpect("_links."+ClassUtils.getShortNameAsProperty(ManyToOneEntityProcessor.class)+".href", ManyToOneEntityProcessor.class.getName())
		.andExpect("_links."+ClassUtils.getShortNameAsProperty(ProcessableProcessor.class)+".href", ProcessableProcessor.class.getName())
		.andReturn("m1");
		

		
		////////////////////////////////////////////////////
		$.POST("/baseEntities").C("name", "base1").C("manyToOneEntity", "{m1.$}").is2xx().andReturn("d1");
	}	
}


