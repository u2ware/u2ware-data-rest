package io.github.u2ware.data.test.mto1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import io.github.u2ware.data.test.RestMockMvc;


@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationExportedTrueTests { 

	protected @Autowired BaseEntityRepository baseEntityRepository;
	protected @Autowired ManyToOneEntityRepository manyToOneEntityRepository;
	protected @Autowired MockMvc mockMvc;
	
	
	@Test
	public void manyToOneTest() throws Exception {
		RestMockMvc $ = new RestMockMvc(mockMvc, "");

		String mtoLink1 = $.POST("/manyToOneEntities").C(ManyToOneEntity.builder().name("mto_a").age(1).build()).is2xx().andReturn().link();		
		String mtoLink2 = $.POST("/manyToOneEntities").C(ManyToOneEntity.builder().name("mto_b").age(2).build()).is2xx().andReturn().link();		
		ManyToOneEntity mtoJson1 = manyToOneEntityRepository.save(ManyToOneEntity.builder().name("mto_c").age(3).build());	
		ManyToOneEntity mtoJson2 = manyToOneEntityRepository.save(ManyToOneEntity.builder().name("mto_d").age(4).build());	
		

		//////////////////////////////
		// POST(Create) -> link(O) json(X)
		//////////////////////////////
		$.POST("/baseEntities").C("name", "e1").C("manyToOneEntity", mtoLink1).is2xx().andReturn("e1"); 
		$.GET("{e1}").is2xx().andExpect("name", "e1").andReturn("e1");
		$.GET("{e1._links.manyToOneEntity.href}").is2xx().andExpect("name", "mto_a");
		

		$.POST("/baseEntities").C("name", "e2").C("manyToOneEntity", mtoJson1).is2xx().andReturn("e2");
		$.GET("{e2}").is2xx().andExpect("name", "mto_c").andReturn("e2"); // Oooooops!!!!!
		$.GET("{e2._links.manyToOneEntity.href}").is4xx();
		
		
		//////////////////////////////
		// PUT (Total Update) -> link(X) json(X)
		//////////////////////////////
		$.PUT("{e1}").C("name", "ee1").C("manyToOneEntity", mtoLink2).is2xx().andReturn("e1");
		$.GET("{e1}").is2xx().andExpect("name", "ee1").andReturn("e1");
		$.GET("{e1._links.manyToOneEntity.href}").is2xx().andExpect("name", "mto_a"); // Not Changed !!!!

		
		$.PUT("{e2}").C("name", "ee2").C("manyToOneEntity", mtoJson2).is2xx().andReturn("e2");
		$.GET("{e2}").is2xx().andExpect("name", "mto_d").andReturn("e2"); // Oooooops!!!!!
		$.GET("{e2._links.manyToOneEntity.href}").is4xx();
		

		//////////////////////////////
		// PATCH (Partial Update) -> link(O) json(X)
		//////////////////////////////
		$.PATCH("{e1}").C("name", "eee1").C("manyToOneEntity", mtoLink2).is2xx().andReturn();
		$.GET("{e1}").is2xx().andExpect("name", "eee1").andReturn("e1");
		$.GET("{e1._links.manyToOneEntity.href}").is2xx().andExpect("name", "mto_b"); 
		
		
		$.PATCH("{e2}").C("name", "eee2").C("manyToOneEntity", mtoJson2).is2xx().andReturn();
		$.GET("{e2}").is2xx().andExpect("name", "mto_d").andReturn("e2"); // // Not Changed !!!!
		$.GET("{e2._links.manyToOneEntity.href}").is4xx();
		
		
		//////////////////////////////////////////////////////
		// properties (text/uri-list) 
		///////////////////////////////////////////////////////
//		curl -i -X PUT -H "Content-Type:text/uri-list" 	-d "http://localhost:8080/libraries/1" http://localhost:8080/books/1/library
		$.PATCH("{e1}/manyToOneEntity").contentType("text/uri-list").C(mtoLink1).is4xx();
		$.PUT("{e1}/manyToOneEntity").contentType("text/uri-list").C(mtoJson1).is5xx(); 
		$.POST("{e1}/manyToOneEntity").contentType("text/uri-list").C(mtoLink1).is4xx(); 
		$.PUT("{e1}/manyToOneEntity").contentType("text/uri-list").C(mtoLink1).is2xx();  //Only put link
		
		$.GET("{e1}/manyToOneEntity").is2xx().andExpect("name", "mto_a");  
		
		
		
	}
}
