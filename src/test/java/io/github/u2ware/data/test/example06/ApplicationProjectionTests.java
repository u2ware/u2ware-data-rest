package io.github.u2ware.data.test.example06;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import io.github.u2ware.data.test.RestMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationProjectionTests {

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
		$.POST("/manyToOneEntities").C(ManyToOneEntity.builder().name("mto_b").age(2).build()).is2xx().andReturn("m2");		
		$.POST("/manyToOneEntities").C(ManyToOneEntity.builder().name("mto_c").age(1).build()).is2xx().andReturn("m3");		
		$.POST("/manyToOneEntities").C(ManyToOneEntity.builder().name("mto_d").age(2).build()).is2xx().andReturn("m4");		
		
		
		$.POST("/baseEntities").C("name", "base1").C("age", "11").is2xx().andReturn("b1");
		
		$.GET("{b1}").is2xx().andExpect("age", 11);
		$.GET("{b1}").P("projection", "baseEntityProjection1").is2xx().andExpect("age", null);
		$.GET("{b1}").P("projection", "baseEntityProjection2").is2xx().andExpect("age", "base1");
	}	
}


