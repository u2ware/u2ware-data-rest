package io.github.u2ware.data.test.mto2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import io.github.u2ware.data.test.RestMockMvc;


@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationExportedFalseTests { 

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
		// POST(Create) -> link(O) json(O)
		//////////////////////////////
		$.POST("/baseEntities").C("name", "e1").C("manyToOneEntity", mtoLink1).is2xx().andReturn("e1");
		$.GET("{e1}").is2xx().andExpect("name", "e1").andExpect("manyToOneEntity.name", "mto_a").andReturn("e1");
		

		$.POST("/baseEntities").C("name", "e2").C("manyToOneEntity", mtoJson1).is2xx().andReturn("e2");
		$.GET("{e2}").is2xx().andExpect("name", "e2").andExpect("manyToOneEntity.name", "mto_c").andReturn("e2");
		
		//////////////////////////////
		// PUT (Total Update) -> link(O) json(O)
		//////////////////////////////
		$.PUT("{e1}").C("name", "ee1").C("manyToOneEntity", mtoLink2).is2xx().andReturn("e1");
		$.GET("{e1}").is2xx().andExpect("name", "ee1").andExpect("manyToOneEntity.name", "mto_b").andReturn("e1");

		$.PUT("{e2}").C("name", "ee2").C("manyToOneEntity", mtoJson2).is2xx().andReturn("e2");
		$.GET("{e2}").is2xx().andExpect("name", "ee2").andExpect("manyToOneEntity.name", "mto_d").andReturn("e2"); 

		
		
		//////////////////////////////
		// PATCH (Partial Update) -> link(O) json(X)
		//////////////////////////////
		$.PATCH("{e1}").C("name", "eee1").C("manyToOneEntity", mtoLink1).is2xx().andReturn();
		$.GET("{e1}").is2xx().andExpect("name", "eee1").andExpect("manyToOneEntity.name", "mto_a").andReturn("e1");
		
		
		//identifier of an instance of '<entity>' was altered from <id> to <id>
		try {
			$.PATCH("{e2}").C("name", "eee2").C("manyToOneEntity",mtoJson1).is2xx().andReturn();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
