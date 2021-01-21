package io.github.u2ware.data.test.otm1;

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
public class ApplicationTests { 

	
	protected Log logger = LogFactory.getLog(getClass());
	
	protected @Autowired BaseEntityRepository baseEntityRepository;
	protected @Autowired OneToManyEntityRepository oneToManyEntityRepository;
	protected @Autowired MockMvc mockMvc;
	
	
	@Test
	public void manyToOneTest() throws Exception {
		RestMockMvc $ = new RestMockMvc(mockMvc, "");
		
//		Set<OneToManyEntity> oneToManyEntities = new HashSet<>();
//		oneToManyEntities.add(OneToManyEntity.builder().name("a").build());
//		oneToManyEntities.add(OneToManyEntity.builder().name("b").build());
//		
////		oneToManyEntities.add(oneToManyEntityRepository.save(OneToManyEntity.builder().name("a").build()));
////		oneToManyEntities.add(oneToManyEntityRepository.save(OneToManyEntity.builder().name("b").build()));
//		
//		$.POST("/baseEntities").H().C("name", "e1-post")
//		.C("manyToOneEntities", oneToManyEntities)
//		.is2xx()
////		.andExpect("manyToOneEntity.name", "mto_a")
//		.andReturn("e1");

		
		
//		//////////////////////////////
//		// POST(Create) -> link(O) json(O)
//		//////////////////////////////
//		$.POST("/baseEntities").H().C("name", "e1-post").C("manyToOneEntity", "{mtoLink1}").is2xx().andExpect("manyToOneEntity.name", "mto_a").andReturn("e1");
//		$.POST("/baseEntities").H().C("name", "e2-post").C("manyToOneEntity", "{mtoJson1.$}").is2xx().andExpect("manyToOneEntity.name", "mto_c").andReturn("e2");
//		
//		
//		//////////////////////////////
//		// PUT(Create) -> link(O) json(O)
//		//////////////////////////////
//		$.PUT("{e1}").H().C("name", "e1-put").C("manyToOneEntity", "{mtoLink2}").is2xx().andExpect("manyToOneEntity.name", "mto_b");
//		$.PUT("{e2}").H().C("name", "e2-put").C("manyToOneEntity", "{mtoJson2.$}").is2xx().andExpect("manyToOneEntity.name", "mto_d");
//		
//		
//		////////////////////////////////////////
//		// PATCH(Create) -> link(O) json(O)
//		///////////////////////////////////////
//		$.PATCH("{e1}").H().C("manyToOneEntity", "{mtoLink1}").is2xx().andExpect("name","e1-put").andExpect("manyToOneEntity.name", "mto_a");
//		$.PATCH("{e1}").H().C("name", "e1-patch").is2xx().andExpect("name","e1-patch").andExpect("manyToOneEntity.name", "mto_a");
//
//		$.PATCH("{e2}").H().C("manyToOneEntity", "{mtoJson1.$}").is2xx().andExpect("name","e2-put").andExpect("manyToOneEntity.name", "mto_c");
//		$.PATCH("{e2}").H().C("name", "e2-patch").is2xx().andExpect("name","e2-patch").andExpect("manyToOneEntity.name", "mto_c");
//		
//		
//		//////////////////////////////////////////
//		// 
//		//////////////////////////////////////////
//		$.GET("{e1}").is2xx().andReturn("e1");
//		$.GET("{e2}").is2xx().andReturn("e2");
//		
//		@SuppressWarnings({ "unchecked", "rawtypes" })
//		Map<String,Object> b1 = (Map)$.variables().resolveValue("{e1.$}"); 
//		b1.put("name", "hello");
//		
//		$.PUT("{e1}").H().C(b1).is2xx().andExpect("name", "hello");
	}
}